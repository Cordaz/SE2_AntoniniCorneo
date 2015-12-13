public final class SecurityMechanismSelector implements PostConstruct {

   /* Evaluates a client's conformance to a security policies
    * at the client authentication layer.
    *
    * returns true if conformant ; else returns false
    */
    private boolean evaluate_client_conformance_ascontext(
                        SecurityContext ctx,
                        EjbIORConfigurationDescriptor iordesc,
                        String realmName)
    {

        boolean client_authenticated = false;

        // get requirements and supports at the client authentication layer
        AS_ContextSec ascontext = null;
        try {
            ascontext = this.getCtc().createASContextSec(iordesc, realmName);
        } catch (Exception e) {
            _logger.log(Level.SEVERE, "iiop.createcontextsec_exception",e);

            return false;
        }
   

        /*************************************************************************
         * Conformance Matrix:
         *
         * |------------|---------------------|---------------------|------------|
         * | ClientAuth | targetrequires.ETIC | targetSupports.ETIC | Conformant |
         * |------------|---------------------|---------------------|------------|
         * |     Yes    |          0          |      1              |    Yes     |
         * |     Yes    |          0          |      0              |    No      |
         * |     Yes    |          1          |      X              |    Yes     |
         * |     No     |          0          |      X              |    Yes     |
         * |     No     |          1          |      X              |    No      |
         * |------------|---------------------|---------------------|------------|
         *
         * Abbreviations: ETIC - EstablishTrusInClient
         * 
         *************************************************************************/

        if  ( (ctx != null) && (ctx.authcls != null) &&  (ctx.subject != null))
            client_authenticated = true;
        else
            client_authenticated = false;

        if (client_authenticated) {
            if ( ! ( isSet(ascontext.target_requires, EstablishTrustInClient.value)
                     || isSet(ascontext.target_supports, EstablishTrustInClient.value))){
            return false; // non conforming client
            }
            // match the target_name from client with the target_name in policy 

            byte [] client_tgtname = getTargetName(ctx.subject);

            if (ascontext.target_name.length != client_tgtname.length){
                return false; // mechanism did not match.
            }            
            for (int i=0; i < ascontext.target_name.length ; i ++)
                if (ascontext.target_name[i] != client_tgtname[i]){
                    return false; // mechanism did not match
                }
        } else { 
            if ( isSet(ascontext.target_requires, EstablishTrustInClient.value)){
                return false;  // no mechanism match.
            }
        }
        return true;
    }

   /* Evaluates a client's conformance to a security policy
    * at the sas context layer. The security policy
    * is derived from the EjbIORConfigurationDescriptor.
    *
    * returns true if conformant ; else returns false
    */
    private boolean evaluate_client_conformance_sascontext(
                        SecurityContext ctx,
                        EjbIORConfigurationDescriptor iordesc)
    {

        boolean caller_propagated = false;

        // get requirements and supports at the sas context layer
        SAS_ContextSec sascontext = null;
        try {
            sascontext = this.getCtc().createSASContextSec(iordesc);
        } catch (Exception e) {
            _logger.log(Level.SEVERE,"iiop.createcontextsec_exception",e);
            return false;
        }

            
        if  ( (ctx != null) && (ctx.identcls != null) &&  (ctx.subject != null))
            caller_propagated = true;
        else
            caller_propagated = false;

        if (caller_propagated) {
            if ( ! isSet(sascontext.target_supports, IdentityAssertion.value))
                return false; // target does not support IdentityAssertion

            /* There is no need further checking here since SecServerRequestInterceptor
             * code filters out the following:
             * a. IdentityAssertions of types other than those required by level 0 
             *    (for e.g. IdentityExtension)
             * b. unsupported identity types.
             * 
             * The checks are done in SecServerRequestInterceptor rather than here
             * to minimize code changes.
             */
            return true;
        }
        return true; //  either caller was not propagated or mechanism matched.
    }
       
                                                    

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
    private boolean evaluate_client_conformance(SecurityContext   ctx,
                                                byte[]            object_id,
                                                boolean           ssl_used,
                                                X509Certificate[] certchain)
    {
        // Obtain the IOR configuration descriptors for the Ejb using
        // the object_id within the SecurityContext field.

        // if object_id is null then nothing to evaluate. This is a sanity
        // check - for the object_id should never be null.
        
        if (object_id == null)
            return true;

        if (protocolMgr == null)
            protocolMgr = orbHelper.getProtocolManager();

        // Check to make sure protocolMgr is not null. 
        // This could happen during server initialization or if this call
        // is on a callback object in the client VM. 
        if (protocolMgr == null)
            return true;

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
        if (iorDescSet.isEmpty())
            return true;

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
        if(checkSkipped)
            return true;
        return false; // No matching security policy found
    }     

}

