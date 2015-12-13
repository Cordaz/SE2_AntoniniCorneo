public final class SecurityMechanismSelector implements PostConstruct {

   /* Evaluates a client's conformance to a security policies
    * at the client authentication layer.
    *
    * returns true if conformant ; else returns false
    */
    private boolean evaluate_client_conformance_ascontext(    # \label{l:108} %
                        SecurityContext ctx,
                        EjbIORConfigurationDescriptor iordesc,
                        String realmName)
    { #\label{l:112}%

        boolean client_authenticated = false;  #\label{l:114}%

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

        if  ( (ctx != null) && (ctx.authcls != null) &&  (ctx.subject != null)) #\label{l:144}%
            client_authenticated = true;
        else
            client_authenticated = false; #\label{l:147}%

        if (client_authenticated) {
            if ( ! ( isSet(ascontext.target_requires, EstablishTrustInClient.value)
                     || isSet(ascontext.target_supports, EstablishTrustInClient.value))){
            return false; // non conforming client
            }
            // match the target_name from client with the target_name in policy 

            byte [] client_tgtname = getTargetName(ctx.subject); #\label{l:156}%

            if (ascontext.target_name.length != client_tgtname.length){
                return false; // mechanism did not match.
            }            
            for (int i=0; i < ascontext.target_name.length ; i ++) #\label{l:161}%
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

}

