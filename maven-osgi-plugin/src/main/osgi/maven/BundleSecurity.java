/*
 * Created on Jun 30, 2005
 */
package osgi.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import sun.misc.BASE64Encoder;

/**
 * @author otmar
 */
public class BundleSecurity {
    
    private String jarName;
    private String obrName;
    private String keyStoreLocation;
    private String keyStoreAlias;
    private String digestGenAlgo;
    private String provider;
    private String keyPassword;

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
    
    
    public void doExecute(){
        try {
            
            // setting default values if not specified
            if ((keyStoreLocation == null) || keyStoreLocation.equals(""))
                keyStoreLocation = System.getProperty("user.home") + File.separatorChar + ".keystore";
            if ((keyStoreAlias == null) || keyStoreAlias.equals(""))
                keyStoreAlias = "maven-osgi";
            if ((keyPassword == null) || keyPassword.equals(""))
                keyPassword = "osgi-pwd";
            if ((digestGenAlgo == null) || digestGenAlgo.equals(""))
                    digestGenAlgo = "SHA1";
            
            RandomAccessFile obrFile = new RandomAccessFile(obrName, "rw");
            byte[] obrContentBytes = new byte[(int) obrFile.length()];
            obrFile.read(obrContentBytes);
            String obrString = new String(obrContentBytes);
            obrString = obrString.substring(0, obrString.indexOf("</bundle>"));
            String securityInfo = getSecurityInfoXML(jarName);
            obrString += XMLHelpers.emitMultilineTagNL("bundle-security", securityInfo, 1);
            obrString += "</bundle>";
            obrContentBytes = obrString.getBytes();
            obrFile.write(obrContentBytes, 0, obrContentBytes.length);
            obrFile.close();
        } catch (Exception e){
            System.out.println("Warning! No signature created for this bundle!");
            e.printStackTrace();
        }
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
        System.out.println("Signing " + deployOSGiJar);
        RandomAccessFile jarFile = new RandomAccessFile(deployOSGiJar, "r");
        byte[] jarBytes = new byte[(int)jarFile.length()];
        jarFile.read(jarBytes);
        
        // get the digest and encode it in base64
        byte[] digestBytes = computeDigest(digestGenAlgo, jarBytes);
        String digestEncoded = new String(Base64.encodeBase64(digestBytes));
        System.out.println("Digest: \n" + digestEncoded);
        
        // create a signature
        PrivateKey privKey = getPrivateKey();
        byte[] signatureBytes = createSignature(privKey, digestBytes);
        String signatureEncoded = new String(Base64.encodeBase64(signatureBytes));
        
        // write it as an XML string
        securityInfoXML = XMLHelpers.emitTag("digestGenerationAlgorithm", digestGenAlgo);
        securityInfoXML += XMLHelpers.emitTag("keyGenerationAlgorithm", privKey.getAlgorithm());
        securityInfoXML += XMLHelpers.emitTag("digest", digestEncoded);
        securityInfoXML += XMLHelpers.emitTag("signature", signatureEncoded);

        // TODO: Debug info
        securityInfoXML += XMLHelpers.emitTag("temp-info", new String(Base64.encodeBase64(getPublicKey().getEncoded())));
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
    
    private PublicKey getPublicKey() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, null);
        return keystore.getCertificate(keyStoreAlias).getPublicKey();
    }
    
    private PrivateKey getPrivateKey() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, null);
        
        return (PrivateKey) keystore.getKey(keyStoreAlias, (keyPassword).toCharArray());
    }
}
