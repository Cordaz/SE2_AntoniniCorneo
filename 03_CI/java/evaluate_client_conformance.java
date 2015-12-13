public final class SecurityMechanismSelector implements PostConstruct {

    /**
     * Evaluates a client's conformance to the security policies configured
     * on the target.
     * Returns true if conformant to the security policies
     * otherwise return false.
     *
     * Conformance checking is done as follows:
     * First, the object_id is mapped to the set of EjbIORConfigurationDescriptor.
     * Each EjbIORConfigurationDescriptor corresponds to a single CompoundSecMechanism
     * of the CSIv2 spec. A client is considered to be conformant if a
CompoundSecMechanism
     * consistent with the client's actions is found i.e. transport_mech,
as_context_mech
     * and sas_context_mech must all be consistent.
     * 
     */
    private boolean evaluate_client_conformance(SecurityContext   ctx,  #\label{l:319} %
                                                byte[]            object_id, #\label{l:320}%
                                                boolean           ssl_used, #\label{l:321}%
                                                X509Certificate[] certchain)
    { #\label{l:323}%
        // Obtain the IOR configuration descriptors for the Ejb using
        // the object_id within the SecurityContext field.

        // if object_id is null then nothing to evaluate. This is a sanity
        // check - for the object_id should never be null.
        
        if (object_id == null)  #\label{l:330}%
            return true;  #\label{l:331}%

        if (protocolMgr == null)  #\label{l:333}%
            protocolMgr = orbHelper.getProtocolManager();  #\label{l:334}%

        // Check to make sure protocolMgr is not null. 
        // This could happen during server initialization or if this call
        // is on a callback object in the client VM. 
        if (protocolMgr == null) #\label{l:339}%
            return true; #\label{l:340}%

        EjbDescriptor ejbDesc = protocolMgr.getEjbDescriptor(object_id);

        Set iorDescSet = null;
        if (ejbDesc != null) {
	    iorDescSet = ejbDesc.getIORConfigurationDescriptors();
	}
	else {
	    // Probably a non-EJB CORBA object.
	    // Create a temporary EjbIORConfigurationDescriptor.
	    iorDescSet = getCorbaIORDescSet();
	}

	if(_logger.isLoggable(Level.FINE)) {
	    _logger.log(Level.FINE,
			"SecurityMechanismSelector.evaluate_client_conformance: iorDescSet: " + iorDescSet);
	}

        /* if there are no IORConfigurationDescriptors configured, then
         * no security policy is configured. So consider the client 
         * to be conformant.
         */
        if (iorDescSet.isEmpty()) #\label{l:363}%
            return true; #\label{l:364}%

        // go through each EjbIORConfigurationDescriptor trying to find
        // a find a CompoundSecMechanism that matches client's actions.
        boolean checkSkipped = false;
        for (Iterator itr = iorDescSet.iterator(); itr.hasNext();) {
            EjbIORConfigurationDescriptor iorDesc = 
                (EjbIORConfigurationDescriptor) itr.next();
            if(skip_client_conformance(iorDesc)){
		if(_logger.isLoggable(Level.FINE)) {
		    _logger.log(Level.FINE,
				"SecurityMechanismSelector.evaluate_client_conformance: skip_client_conformance");
		}
                checkSkipped = true;
                continue;
            }
            if (! evaluate_client_conformance_ssl(iorDesc, ssl_used, certchain)){
		if(_logger.isLoggable(Level.FINE)) {
		    _logger.log(Level.FINE,
				"SecurityMechanismSelector.evaluate_client_conformance: evaluate_client_conformance_ssl");
		}
                checkSkipped = false;
                continue;
            }
            String realmName = "default";
            if(ejbDesc != null && ejbDesc.getApplication() != null) {
                realmName = ejbDesc.getApplication().getRealm();
            }
            if(realmName == null) {
                realmName = iorDesc.getRealmName();
            }
            if (realmName == null) {
                realmName = "default";
            }
            if ( ! evaluate_client_conformance_ascontext(ctx, iorDesc ,realmName)){
		if(_logger.isLoggable(Level.FINE)) {
		    _logger.log(Level.FINE,
				"SecurityMechanismSelector.evaluate_client_conformance: evaluate_client_conformance_ascontext");
		}
                checkSkipped = false;
                continue;
            }
            if  ( ! evaluate_client_conformance_sascontext(ctx, iorDesc)){
		if(_logger.isLoggable(Level.FINE)) {
		    _logger.log(Level.FINE,
				"SecurityMechanismSelector.evaluate_client_conformance: evaluate_client_conformance_sascontext");
		}
               checkSkipped = false;
               continue;
            }
            return true;  // security policy matched.
        }
        if(checkSkipped) #\label{l:3116}%
            return true; #\label{l:3117}%
        return false; // No matching security policy found
    }     

}

