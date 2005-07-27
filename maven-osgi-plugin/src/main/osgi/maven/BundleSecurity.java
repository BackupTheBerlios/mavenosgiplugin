/*
 * Created on Jun 30, 2005
 */
package osgi.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    private String showPublicKey;

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
    public String getShowPublicKey() {
		return showPublicKey;
	}
	public void setShowPublicKey(String showPublicKey) {
		this.showPublicKey = showPublicKey;
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
            if ((keyStoreType == null) || keyStoreType.trim().equals(""))
                keyStoreType = KeyStore.getDefaultType();
            
            certificate = getCertificate();
            
            writeObr();
            
        } catch (Exception e){
            System.out.println("Warning! No signature created for this bundle!");
            e.printStackTrace();
        }
    }
    
    private void writeObr() throws Exception{
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
    }
    
    private String getSecurityInfoXML(String deployOSGiJar) throws Exception {
        
        String securityInfoXML = "";
        
        // check if an additional security provider is given and if yes, install it
        if ((provider != null) && !provider.equals("")){
            try {
                Object providerInst = Class.forName(provider).newInstance();
        	    Security.addProvider((Provider)providerInst);
            } catch (Exception e){
                System.out.println("Warning! Could not install the additional provider "+ provider);
            }
        }
        
        // if a digestAlgorithm is given, we compute the digest
        if ((digestGenAlgo != null) && !digestGenAlgo.trim().equals("")){
            InputStream toSign = new FileInputStream(deployOSGiJar);
            byte[] buffer = new byte[BUFFERSIZE];
            MessageDigest msgDigest = MessageDigest.getInstance(digestGenAlgo);
            int length;
            while ((length =toSign.read(buffer)) != -1){
                msgDigest.update(buffer, 0, length);
            }
            byte[] digest = msgDigest.digest();
            String digestEncoded = new String(Base64.encodeBase64(digest));
            securityInfoXML += XMLHelpers.emitTag("digestGenerationAlgorithm", digestGenAlgo);
            securityInfoXML += XMLHelpers.emitTag("digest", digestEncoded);
        }
        
        // compute signature
        Signature sig = Signature.getInstance(certificate.getPublicKey().getAlgorithm());
        sig.initSign(getPrivateKey());
        InputStream toSign = new FileInputStream(deployOSGiJar);
        byte[] buffer = new byte[BUFFERSIZE];
        int length;
        while ((length =toSign.read(buffer)) != -1){
            sig.update(buffer, 0, length);
        }
        String signatureEncoded = new String(Base64.encodeBase64(sig.sign()));
        securityInfoXML += XMLHelpers.emitTag("signature", signatureEncoded);
        String encodedCert = new String(Base64.encodeBase64(certificate.getEncoded()));
        encodedCert = XMLHelpers.insertNL(encodedCert, 60);
        securityInfoXML += XMLHelpers.emitMultilineTagNL("certificate", encodedCert);
        if (showPublicKey != null && showPublicKey.equals("true")){
        	String pubKey = new String(Base64.encodeBase64(certificate.getPublicKey().getEncoded()));
            pubKey = XMLHelpers.insertNL(pubKey, 60);
            System.out.println("Public key Base64 encoded:\n" + pubKey);
        }
        return securityInfoXML;
    }
    
    private X509Certificate getCertificate() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(is, null);
        return (X509Certificate)keystore.getCertificate(keyStoreAlias);
    }
    
    private PrivateKey getPrivateKey() throws Exception{
        File file = new File(keyStoreLocation);
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, null);
        return (PrivateKey) keystore.getKey(keyStoreAlias, (keyPassword).toCharArray());
    }
}
