public final class SecurityMechanismSelector implements PostConstruct {

   /* Evaluates a client's conformance to a security policy
    * at the sas context layer. The security policy
    * is derived from the EjbIORConfigurationDescriptor.
    *
    * returns true if conformant ; else returns false
    */
    private boolean evaluate_client_conformance_sascontext(  # \label{l:209} %
                        SecurityContext ctx,
                        EjbIORConfigurationDescriptor iordesc)
    { #\label{l:212}%

        boolean caller_propagated = false;  #\label{l:214}%

        // get requirements and supports at the sas context layer
        SAS_ContextSec sascontext = null;
        try {
            sascontext = this.getCtc().createSASContextSec(iordesc);
        } catch (Exception e) {
            _logger.log(Level.SEVERE,"iiop.createcontextsec_exception",e);
            return false;
        }

            
        if  ( (ctx != null) && (ctx.identcls != null) &&  (ctx.subject != null)) #\label{l:226}%
            caller_propagated = true;
        else
            caller_propagated = false; #\label{l:229}%

        if (caller_propagated) {
            if ( ! isSet(sascontext.target_supports, IdentityAssertion.value)) #\label{l:232}%
                return false; #\label{l:233}%// target does not support IdentityAssertion

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

}

