
/**
 * This class is responsible for making various decisions for selecting
 * security information to be sent in the IIOP message based on target
 * configuration and client policies.
 * Note: This class can be called concurrently by multiple client threads.
 * However, none of its methods need to be synchronized because the methods
 * either do not modify state or are idempotent.
 *
 * @author Nithya Subramanian
 *
 */

@Service
@Singleton
public final class SecurityMechanismSelector implements PostConstruct {

    private static final java.util.logging.Logger _logger
            = LogDomains.getLogger(SecurityMechanismSelector.class, LogDomains.SECURITY_LOGGER);

    public static final String CLIENT_CONNECTION_CONTEXT = "ClientConnContext";

    private Set<EjbIORConfigurationDescriptor> corbaIORDescSet = null;
    private boolean sslRequired = false;

    private static final LocalStringManagerImpl localStrings
            = new LocalStringManagerImpl(SecServerRequestInterceptor.class);

    private ProtocolManager protocolMgr = null;

    @Inject
    private SSLUtils sslUtils;

    private GlassFishORBHelper orbHelper;

    private ORB orb = null;
    private CSIV2TaggedComponentInfo ctc = null;

    @Inject
    private InvocationManager invMgr;

    @Inject
    private ProcessEnvironment processEnv;

    /**
     * Read the client and server preferences from the config files.
     */
    public SecurityMechanismSelector() {
        //...
    }

    public void postConstruct() {
        //...
    }

    public ConnectionContext getClientConnectionContext() {
        //...
    }

    public void setClientConnectionContext(ConnectionContext scc) {
        //...
    }

    /**
     * This method determines if SSL should be used to connect to the target
     * based on client and target policies. It will return null if SSL should
     * not be used or an SocketInfo containing the SSL port if SSL should be
     * used.
     */
    public SocketInfo getSSLPort(IOR ior, ConnectionContext ctx) {
        //...
    }

    /*
    public String[] getServerTrustedHosts() {
		//...
}
    
    public void setServerTrustedHosts(String[] val) {
		//...
}
     */
    public ORB getOrb() {
        //...
    }

    public void setOrb(ORB val) {
        //...
    }

    public synchronized CSIV2TaggedComponentInfo getCtc() {
        //...
    }

    public java.util.List<SocketInfo> getSSLPorts(IOR ior, ConnectionContext ctx) {
        //...
    }

    /**
     * Select the security context to be used by the CSIV2 layer based on
     * whether the current component is an application client or a web/EJB
     * component.
     */
    public SecurityContext selectSecurityContext(IOR ior)
            throws InvalidIdentityTokenException,
            InvalidMechanismException, SecurityMechanismException {
        //...
    }

    /**
     * Create the security context to be used by the CSIV2 layer to marshal in
     * the service context of the IIOP message from an appclient or standalone
     * client.
     *
     * @return the security context.
     */
    public SecurityContext getSecurityContextForAppClient(
            ComponentInvocation ci,
            boolean sslUsed,
            boolean clientAuthOccurred,
            CompoundSecMech mechanism)
            throws InvalidMechanismException, InvalidIdentityTokenException,
            SecurityMechanismException {
        //...
    }

    /**
     * Create the security context to be used by the CSIV2 layer to marshal in
     * the service context of the IIOP message from an web component or EJB
     * invoking another EJB.
     *
     * @return the security context.
     */
    public SecurityContext getSecurityContextForWebOrEJB(
            ComponentInvocation ci, boolean sslUsed,
            boolean clientAuthOccurred,
            CompoundSecMech mechanism)
            throws InvalidMechanismException, InvalidIdentityTokenException,
            SecurityMechanismException {
        //...
    }

    Object getSSLSocketInfo(Object ior) {
        //...
    }

    private boolean isMechanismSupported(SAS_ContextSec sas) {
        //...
    }

    public boolean isIdentityTypeSupported(SAS_ContextSec sas) {
        //...
    }

    /**
     * Get the security context to send username and password in the service
     * context.
     *
     * @param whether username/password will be sent over plain IIOP or over
     * IIOP/SSL.
     * @return the security context.
     * @exception SecurityMechanismException if there was an error.
     */
    private SecurityContext sendUsernameAndPassword(ComponentInvocation ci,
            boolean sslUsed,
            boolean clientAuthOccurred,
            CompoundSecMech mechanism)
            throws SecurityMechanismException {
        //...
    }

    /**
     * Get the security context to propagate principal/distinguished name in the
     * service context.
     *
     * @param clientAuth whether SSL client authentication has happened.
     * @return the security context.
     * @exception SecurityMechanismException if there was an error.
     */
    private SecurityContext propagateIdentity(boolean clientAuth,
            ComponentInvocation ci,
            CompoundSecMech mechanism)
            throws InvalidIdentityTokenException, InvalidMechanismException, SecurityMechanismException {
        //...
    }

    /**
     * Return whether the server is trusted or not based on configuration
     * information.
     *
     * @return true if the server is trusted.
     */
    /*
    private boolean isServerTrusted() {
		//...
}
     */
    /**
     * Checks if a given domain is trusted. e.g. domain = 123.203.1.1 is an IP
     * address trusted list = *.com, *.eng should say that the given domain is
     * trusted.
     *
     * @param the InetAddress of the domain to be checked for
     * @param the array of trusted domains
     * @return true - if the given domain is trusted
     */
    /*
    private boolean isDomainInTrustedList (InetAddress inetAddress,
                                           String[] trusted)
        throws SecurityException 
    {
		//...
}
     */
    /**
     * Get the username and password either from the JAAS subject or from thread
     * local storage. For appclients if login has'nt happened this method would
     * trigger login and popup a user interface to gather authentication
     * information.
     *
     * @return the security context.
     */
    private SecurityContext getUsernameAndPassword(ComponentInvocation ci, CompoundSecMech mechanism)
            throws SecurityMechanismException {
        //...
    }

    /**
     * Get the principal/distinguished name from thread local storage.
     *
     * @return the security context.
     */
    private SecurityContext getIdentity()
            throws SecurityMechanismException {
        //...
    }

    private Subject getSubjectFromSecurityCurrent()
            throws SecurityMechanismException {
        //...
    }

    public CompoundSecMech selectSecurityMechanism(IOR ior)
            throws SecurityMechanismException {
        //...
    }

    /**
     * Select the security mechanism from the list of compound security
     * mechanisms.
     */
    private CompoundSecMech selectSecurityMechanism(
            CompoundSecMech[] mechList) throws SecurityMechanismException {
        //...
    }

    private boolean useMechanism(CompoundSecMech mech) {
        //...
    }

    private boolean isMechanismSupportedAS(AS_ContextSec as) {
        //...
    }

    private byte[] getTargetName(Subject subj) {
        //...
    }

    private boolean evaluate_client_conformance_ssl(
            EjbIORConfigurationDescriptor iordesc,
            boolean ssl_used,
            X509Certificate[] certchain) {
        //...
    }

    /* Evaluates a client's conformance to a security policies
    * at the client authentication layer.
    *
    * returns true if conformant ; else returns false
     */
    private boolean evaluate_client_conformance_ascontext(
            SecurityContext ctx,
            EjbIORConfigurationDescriptor iordesc,
            String realmName) {
        //...
    }

    /* Evaluates a client's conformance to a security policy
    * at the sas context layer. The security policy
    * is derived from the EjbIORConfigurationDescriptor.
    *
    * returns true if conformant ; else returns false
     */
    private boolean evaluate_client_conformance_sascontext(
            SecurityContext ctx,
            EjbIORConfigurationDescriptor iordesc) {
        //...
    }

    /**
     * Evaluates a client's conformance to the security policies configured on
     * the target. Returns true if conformant to the security policies otherwise
     * return false.
     *
     * Conformance checking is done as follows: First, the object_id is mapped
     * to the set of EjbIORConfigurationDescriptor. Each
     * EjbIORConfigurationDescriptor corresponds to a single
     * CompoundSecMechanism of the CSIv2 spec. A client is considered to be
     * conformant if a CompoundSecMechanism consistent with the client's actions
     * is found i.e. transport_mech, as_context_mech and sas_context_mech must
     * all be consistent.
     *
     */
    private boolean evaluate_client_conformance(SecurityContext ctx,
            byte[] object_id,
            boolean ssl_used,
            X509Certificate[] certchain) {
        //...
    }

    /**
     * if ejb requires no security - then skip checking the client-conformance
     */
    private boolean skip_client_conformance(EjbIORConfigurationDescriptor ior) {
        //...
    }

    /**
     * Called by the target to interpret client credentials after validation.
     */
    public SecurityContext evaluateTrust(SecurityContext ctx, byte[] object_id, Socket socket)
            throws SecurityMechanismException {
        //...
    }

    private static boolean isSet(int val1, int val2) {
        //...
    }

    private Set<EjbIORConfigurationDescriptor> getCorbaIORDescSet() {
        //...
    }

    public boolean isSslRequired() {
        //...
    }

    private boolean isNotServerOrACC() {
        //...
    }

    private boolean isACC() {
        //...
    }

    private static final String traceIORsProperty
            = "com.sun.enterprise.iiop.security.traceIORS";

    private static final boolean _traceIORs = Boolean.getBoolean(traceIORsProperty);

    private static final Hashtable<Integer, String> assocOptions;

    static {
        //...
    }

    private static final Hashtable<Integer, String> identityTokenTypes;

    static {
        //...
    }

    public static boolean traceIORs() {
        //...
    }

    public String getSecurityMechanismString(CSIV2TaggedComponentInfo tCI,
            IOR ior) {
        //...
    }

    public static String getSecurityMechanismString(CSIV2TaggedComponentInfo tCI,
            CompoundSecMech[] list, String name) {
        //...
    }

}
