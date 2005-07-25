/*
 * Created on Jun 30, 2005
 */
package osgi.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * @author otmar
 */
public class BundleSecurity {
    
    private static final int BUFFERSIZE = 1024;
    private static final String certDir = "cert";
    private static final String certSuffix = ".cer";
    
    private X509Certificate certificate;
    
    private String jarName;
    private String obrName;
    private String keyStoreLocation;
    private String keyStoreType;
    private String keyStoreAlias;
    private String digestGenAlgo;
    private String provider;
    private String keyPassword;
    private String repoLocation;

    public String getDigestGenAlgo() {
        return digestGenAlgo;
    }
    public void setDigestGenAlgo(String digestGenAlgo) {
        this.digestGenAlgo = digestGenAlgo;
    }
    public String getJarName() {
        return jarName;
    }
    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
    public String getKeyPassword() {
        return keyPassword;
    }
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }
    public void setKeyStoreAlias(String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }
    public String getKeyStoreLocation() {
        return keyStoreLocation;
    }
    public void setKeyStoreLocation(String keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }
    public String getKeyStoreType() {
        return keyStoreType;
    }
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    public String getObrName() {
        return obrName;
    }
    public void setObrName(String obrName) {
        this.obrName = obrName;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getRepoLocation() {
        return repoLocation;
    }
    public void setRepoLocation(String repoLocation) {
        this.repoLocation = repoLocation;
    }
    
    public void doExecute(){
        try {
            
            // setting default values if not specified
            if ((keyStoreLocation == null) || keyStoreLocation.trim().equals(""))
                keyStoreLocation = System.getProperty("user.home") + File.separatorChar + ".keystore";
            if ((keyStoreAlias == null) || keyStoreAlias.trim().equals(""))
                keyStoreAlias = "maven-osgi";
            if ((keyPassword == null) || keyPassword.trim().equals(""))
                keyPassword = "osgi-pwd";
            if ((digestGenAlgo == null) || digestGenAlgo.trim().equals(""))
                    digestGenAlgo = "SHA1";
            if ((keyStoreType == null) || keyStoreType.trim().equals(""))
                keyStoreType = KeyStore.getDefaultType();
            
            certificate = getCertificate();
            
            RandomAccessFile obrFile = new RandomAccessFile(obrName, "rw");
            byte[] obrContentBytes = new byte[(int) obrFile.length()];
            obrFile.read(obrContentBytes);
            String obrString = new String(obrContentBytes);
            obrString = obrString.substring(0, obrString.indexOf("</bundle>"));
            String securityInfo = getSecurityInfoXML(jarName);
            obrString += XMLHelpers.emitMultilineTagNL("bundle-security", securityInfo, 1);
            obrString += "</bundle>";
            obrContentBytes = obrString.getBytes();
            obrFile.seek(0);
            obrFile.write(obrContentBytes, 0, obrContentBytes.length);
            obrFile.close();
            deployCertificate();
        } catch (Exception e){
            System.out.println("Warning! No signature created for this bundle!");
            e.printStackTrace();
        }
    }
    
    private X509Certificate getCertificate() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(is, null);
        return (X509Certificate)keystore.getCertificate(keyStoreAlias);
    }
    
    /**
     * @param deployOSGiJar
     * @return
     */
    private String getSecurityInfoXML(String deployOSGiJar) throws Exception {
        
        String securityInfoXML = new String();
        
        // check if an additional security provider is given and if yes, install it
        if ((provider != null) && !provider.equals("")){
            try {
                Object providerInst = Class.forName(provider).newInstance();
        	    Security.addProvider((Provider)providerInst);
            } catch (Exception e){
                System.out.println("Warning! Could not install the additional provider "+ provider);
            }
        }
        
        // open the JAR file and read it
        //RandomAccessFile jarFile = new RandomAccessFile(deployOSGiJar, "r");
        FileInputStream jarFile = new FileInputStream(deployOSGiJar);
        byte[] buffer = new byte[BUFFERSIZE];
        
        // get the digest and encode it in base64
        // byte[] digestBytes = computeDigest(digestGenAlgo, jarBytes);
        // String digestEncoded = new String(Base64.encodeBase64(digestBytes));
        
        // compute digest & signature
        MessageDigest msgDigest = MessageDigest.getInstance(digestGenAlgo);
        Signature sig = Signature.getInstance(certificate.getPublicKey().getAlgorithm());
        sig.initSign(getPrivateKey());
        int length;
        while ((length =jarFile.read(buffer)) != -1){
            sig.update(buffer, 0, length);
            msgDigest.update(buffer, 0, length);
        }
        String digestEncoded = new String(Base64.encodeBase64(msgDigest.digest()));
        String signatureEncoded = new String(Base64.encodeBase64(sig.sign()));
        
        //byte[] signatureBytes = createSignature(privKey, digestBytes);
        //byte[] signatureBytes = createSignature(privKey, jarBytes);
        //String signatureEncoded = new String(Base64.encodeBase64(signatureBytes));
        
        // write it as an XML string
        securityInfoXML = XMLHelpers.emitTag("digestGenerationAlgorithm", digestGenAlgo);
        //securityInfoXML += XMLHelpers.emitTag("keyGenerationAlgorithm", privKey.getAlgorithm());
        securityInfoXML += XMLHelpers.emitTag("digest", digestEncoded);
        System.out.println("digest: " + digestEncoded);
        securityInfoXML += XMLHelpers.emitTag("signature", signatureEncoded);
        System.out.println("signature: " + signatureEncoded);
        String encodedCert = new String(Base64.encodeBase64(certificate.getEncoded()));
        securityInfoXML += XMLHelpers.emitTag("certificate", encodedCert);
        // TODO: Debug info
        //securityInfoXML += XMLHelpers.emitTag("temp-info", new String(Base64.encodeBase64(getPublicKey().getEncoded())));
        return securityInfoXML;
    }
    
    private byte[] computeDigest(String msgDigestAlgo, byte[] buffer) throws Exception {
        MessageDigest msgDigest = MessageDigest.getInstance(msgDigestAlgo);
        msgDigest.update(buffer);
        return msgDigest.digest();
    }
    
    // Returns the signature for the given buffer of bytes using the private key.
    private byte[] createSignature(PrivateKey key, byte[] buffer) throws Exception {
        Signature sig = Signature.getInstance(key.getAlgorithm());
        sig.initSign(key);
        sig.update(buffer, 0, buffer.length);
        return sig.sign();
    }
    
    private PrivateKey getPrivateKey() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, null);
        return (PrivateKey) keystore.getKey(keyStoreAlias, (keyPassword).toCharArray());
    }
    
    private void deployCertificate() throws Exception {
        File directory = new File(repoLocation + File.separator + certDir);
        if (!directory.exists()){
            directory.mkdir();
        }
        String certPath = repoLocation + File.separator + certDir + File.separator + certificate.getSerialNumber() + certSuffix;
        File certFile = new File(certPath);
        certFile.createNewFile();
        FileOutputStream os = new FileOutputStream(certFile);
        os.write(certificate.getEncoded());
        os.flush();
        os.close();
    }
}
