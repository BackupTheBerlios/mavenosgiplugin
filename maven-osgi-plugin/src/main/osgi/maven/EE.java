/*
 * Copyright (c) 2003, KNOPFLERFISH project All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the KNOPFLERFISH project nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package osgi.maven;

import java.util.Hashtable;

/**
 * Utility class with static methods for checking classes with the standard OSGi
 * profiles. See r3.book.pdf f�r details.
 */
public class EE
{

    final static String[] EE_MINIMUM = { "java.io.BufferedInputStream",
            "java.io.FilterInputStream", "java.io.InputStream",
            "java.io.IOException", "java.io.Serializable",
            "java.io.PrintStream", "java.io.FilterOutputStream",
            "java.io.OutputStream", "java.io.PrintWriter", "java.io.Writer",
            "java.io.UnsupportedEncodingException",
            "java.io.BufferedOutputStream", "java.io.BufferedReader",
            "java.io.Reader", "java.io.BufferedWriter",
            "java.io.ByteArrayInputStream", "java.io.ByteArrayOutputStream",
            "java.io.CharConversionException", "java.io.DataInput",
            "java.io.DataInputStream", "java.io.DataOutput",
            "java.io.DataOutputStream", "java.io.EOFException",
            "java.io.Externalizable", "java.io.ObjectInput",
            "java.io.ObjectOutput", "java.io.File", "java.io.FileDescriptor",
            "java.io.SyncFailedException", "java.io.FileInputStream",
            "java.io.FileNotFoundException", "java.io.FileOutputStream",
            "java.io.FilePermission", "java.io.FileReader",
            "java.io.InputStreamReader", "java.io.FileWriter",
            "java.io.OutputStreamWriter", "java.io.InterruptedIOException",
            "java.io.InvalidClassException", "java.io.ObjectStreamException",
            "java.io.InvalidObjectException", "java.io.NotActiveException",
            "java.io.NotSerializableException",
            "java.io.ObjectInputStream$GetField", "java.io.ObjectInputStream",
            "java.io.ObjectStreamConstants",
            "java.io.StreamCorruptedException", "java.io.ObjectStreamClass",
            "java.io.OptionalDataException", "java.io.ObjectInputValidation",
            "java.io.SerializablePermission", "java.io.ObjectStreamField",
            "java.io.ObjectOutputStream$PutField",
            "java.io.ObjectOutputStream", "java.io.PushbackInputStream",
            "java.io.RandomAccessFile", "java.io.StringReader",
            "java.io.StringWriter", "java.io.UTFDataFormatException",
            "java.io.WriteAbortedException", "java.lang.ref.PhantomReference",
            "java.lang.ref.Reference", "java.lang.ref.ReferenceQueue",
            "java.lang.ref.SoftReference", "java.lang.ref.WeakReference",
            "java.lang.reflect.Constructor",
            "java.lang.reflect.AccessibleObject", "java.lang.reflect.Member",
            "java.lang.reflect.Field", "java.lang.reflect.Method",
            "java.lang.reflect.InvocationTargetException",
            "java.lang.reflect.Array", "java.lang.reflect.InvocationHandler",
            "java.lang.reflect.Modifier", "java.lang.reflect.Proxy",
            "java.lang.reflect.ReflectPermission",
            "java.lang.reflect.UndeclaredThrowableException",
            "java.lang.Object", "java.lang.Exception", "java.lang.Throwable",
            "java.lang.CloneNotSupportedException", "java.lang.Class",
            "java.lang.String", "java.lang.Comparable",
            "java.lang.InterruptedException",
            "java.lang.ClassNotFoundException", "java.lang.ClassLoader",
            "java.lang.NoSuchMethodException", "java.lang.SecurityException",
            "java.lang.RuntimeException", "java.lang.NoSuchFieldException",
            "java.lang.IllegalAccessException",
            "java.lang.InstantiationException", "java.lang.ClassFormatError",
            "java.lang.LinkageError", "java.lang.Error",
            "java.lang.IllegalArgumentException", "java.lang.StringBuffer",
            "java.lang.AbstractMethodError",
            "java.lang.IncompatibleClassChangeError",
            "java.lang.ArithmeticException",
            "java.lang.ArrayIndexOutOfBoundsException",
            "java.lang.IndexOutOfBoundsException",
            "java.lang.ArrayStoreException", "java.lang.Boolean",
            "java.lang.Byte", "java.lang.Number",
            "java.lang.NumberFormatException", "java.lang.Character",
            "java.lang.ClassCastException", "java.lang.ClassCircularityError",
            "java.lang.Cloneable", "java.lang.Double",
            "java.lang.ExceptionInInitializerError", "java.lang.Float",
            "java.lang.IllegalAccessError",
            "java.lang.IllegalMonitorStateException",
            "java.lang.IllegalStateException",
            "java.lang.IllegalThreadStateException",
            "java.lang.InstantiationError", "java.lang.Integer",
            "java.lang.InternalError", "java.lang.VirtualMachineError",
            "java.lang.Long", "java.lang.Math",
            "java.lang.NegativeArraySizeException",
            "java.lang.NoClassDefFoundError", "java.lang.NoSuchFieldError",
            "java.lang.NoSuchMethodError", "java.lang.NullPointerException",
            "java.lang.OutOfMemoryError", "java.lang.Process",
            "java.lang.Runnable", "java.lang.Runtime",
            "java.lang.RuntimePermission", "java.lang.SecurityManager",
            "java.lang.Thread", "java.lang.ThreadGroup", "java.lang.Short",
            "java.lang.System", "java.lang.Void",
            "java.lang.StackOverflowError",
            "java.lang.StringIndexOutOfBoundsException",
            "java.lang.ThreadDeath", "java.lang.UnknownError",
            "java.lang.UnsatisfiedLinkError",
            "java.lang.UnsupportedOperationException", "java.lang.VerifyError",
            "java.net.URL", "java.net.URLStreamHandlerFactory",
            "java.net.MalformedURLException", "java.net.URLStreamHandler",
            "java.net.URLConnection", "java.net.InetAddress",
            "java.net.FileNameMap", "java.net.ContentHandlerFactory",
            "java.net.UnknownHostException", "java.net.ContentHandler",
            "java.net.Authenticator", "java.net.PasswordAuthentication",
            "java.net.BindException", "java.net.SocketException",
            "java.net.ConnectException", "java.net.DatagramPacket",
            "java.net.DatagramSocket", "java.net.DatagramSocketImpl",
            "java.net.SocketOptions", "java.net.HttpURLConnection",
            "java.net.ProtocolException", "java.net.JarURLConnection",
            "java.net.MulticastSocket", "java.net.NetPermission",
            "java.net.NoRouteToHostException", "java.net.ServerSocket",
            "java.net.Socket", "java.net.SocketImpl",
            "java.net.SocketImplFactory", "java.net.SocketPermission",
            "java.net.URLDecoder", "java.net.URLEncoder",
            "java.net.UnknownServiceException",
            "java.security.cert.Certificate", "java.security.ProtectionDomain",
            "java.security.CodeSource", "java.security.PermissionCollection",
            "java.security.Permission", "java.security.BasicPermission",
            "java.security.AccessControlContext",
            "java.security.AccessControlException",
            "java.security.AccessController", "java.security.PrivilegedAction",
            "java.security.PrivilegedExceptionAction",
            "java.security.PrivilegedActionException",
            "java.security.AllPermission", "java.security.Permissions",
            "java.security.Policy", "java.security.Principal",
            "java.security.Security", "java.security.SecurityPermission",
            "java.security.UnresolvedPermission", "java.util.zip.CRC32",
            "java.util.zip.Checksum", "java.util.zip.DataFormatException",
            "java.util.zip.Inflater", "java.util.zip.InflaterInputStream",
            "java.util.zip.ZipConstants", "java.util.zip.ZipEntry",
            "java.util.zip.ZipException", "java.util.zip.ZipFile",
            "java.util.zip.ZipInputStream", "java.util.Comparator",
            "java.util.Enumeration", "java.util.Properties",
            "java.util.Hashtable", "java.util.Dictionary",
            "java.util.Map$Entry", "java.util.Map", "java.util.Set",
            "java.util.Collection", "java.util.Iterator",
            "java.util.AbstractCollection", "java.util.AbstractList",
            "java.util.List", "java.util.ListIterator",
            "java.util.AbstractMap", "java.util.AbstractSequentialList",
            "java.util.AbstractSet", "java.util.ArrayList", "java.util.Arrays",
            "java.util.Calendar", "java.util.TimeZone", "java.util.Date",
            "java.util.Collections", "java.util.Random", "java.util.SortedMap",
            "java.util.SortedSet", "java.util.ConcurrentModificationException",
            "java.util.EventListener", "java.util.EventObject",
            "java.util.GregorianCalendar", "java.util.ListResourceBundle",
            "java.util.ResourceBundle", "java.util.MissingResourceException",
            "java.util.Locale", "java.util.NoSuchElementException",
            "java.util.PropertyPermission", "java.util.PropertyResourceBundle",
            "java.util.SimpleTimeZone", "java.util.StringTokenizer",
            "java.util.Vector", "java.util.WeakHashMap",};

    final static String[] EE_FOUNDATION = { "java.io.BufferedInputStream",
            "java.io.FilterInputStream", "java.io.InputStream",
            "java.io.IOException", "java.io.Serializable",
            "java.io.PrintStream", "java.io.FilterOutputStream",
            "java.io.OutputStream", "java.io.PrintWriter", "java.io.Writer",
            "java.io.UnsupportedEncodingException",
            "java.io.ObjectStreamException", "java.io.BufferedOutputStream",
            "java.io.BufferedReader", "java.io.Reader",
            "java.io.BufferedWriter", "java.io.ByteArrayInputStream",
            "java.io.ByteArrayOutputStream", "java.io.CharArrayReader",
            "java.io.CharArrayWriter", "java.io.CharConversionException",
            "java.io.DataInput", "java.io.DataInputStream",
            "java.io.DataOutput", "java.io.DataOutputStream",
            "java.io.EOFException", "java.io.Externalizable",
            "java.io.ObjectInput", "java.io.ObjectOutput", "java.io.File",
            "java.io.FilenameFilter", "java.io.FileFilter",
            "java.io.FileDescriptor", "java.io.SyncFailedException",
            "java.io.FileInputStream", "java.io.FileNotFoundException",
            "java.io.FileOutputStream", "java.io.FilePermission",
            "java.io.FileReader", "java.io.InputStreamReader",
            "java.io.FileWriter", "java.io.OutputStreamWriter",
            "java.io.FilterReader", "java.io.FilterWriter",
            "java.io.InterruptedIOException", "java.io.InvalidClassException",
            "java.io.InvalidObjectException", "java.io.LineNumberReader",
            "java.io.NotActiveException", "java.io.NotSerializableException",
            "java.io.ObjectInputStream$GetField", "java.io.ObjectInputStream",
            "java.io.ObjectStreamConstants",
            "java.io.StreamCorruptedException", "java.io.ObjectStreamClass",
            "java.io.OptionalDataException", "java.io.ObjectInputValidation",
            "java.io.SerializablePermission", "java.io.ObjectStreamField",
            "java.io.ObjectOutputStream",
            "java.io.ObjectOutputStream$PutField", "java.io.PipedInputStream",
            "java.io.PipedOutputStream", "java.io.PipedReader",
            "java.io.PipedWriter", "java.io.PushbackInputStream",
            "java.io.PushbackReader", "java.io.RandomAccessFile",
            "java.io.SequenceInputStream", "java.io.StreamTokenizer",
            "java.io.StringReader", "java.io.StringWriter",
            "java.io.UTFDataFormatException", "java.io.WriteAbortedException",
            "java.lang.ref.PhantomReference", "java.lang.ref.Reference",
            "java.lang.ref.ReferenceQueue", "java.lang.ref.SoftReference",
            "java.lang.ref.WeakReference", "java.lang.reflect.Constructor",
            "java.lang.reflect.AccessibleObject", "java.lang.reflect.Member",
            "java.lang.reflect.Field", "java.lang.reflect.Method",
            "java.lang.reflect.InvocationTargetException",
            "java.lang.reflect.Array", "java.lang.reflect.InvocationHandler",
            "java.lang.reflect.Modifier", "java.lang.reflect.Proxy",
            "java.lang.reflect.ReflectPermission",
            "java.lang.reflect.UndeclaredThrowableException",
            "java.lang.Object", "java.lang.Exception", "java.lang.Throwable",
            "java.lang.CloneNotSupportedException", "java.lang.Class",
            "java.lang.String", "java.lang.Comparable",
            "java.lang.InterruptedException",
            "java.lang.ClassNotFoundException", "java.lang.ClassLoader",
            "java.lang.NoSuchMethodException", "java.lang.SecurityException",
            "java.lang.RuntimeException", "java.lang.NoSuchFieldException",
            "java.lang.IllegalAccessException",
            "java.lang.InstantiationException", "java.lang.Package",
            "java.lang.StringBuffer", "java.lang.Cloneable",
            "java.lang.ClassFormatError", "java.lang.LinkageError",
            "java.lang.Error", "java.lang.IllegalArgumentException",
            "java.lang.NumberFormatException", "java.lang.AbstractMethodError",
            "java.lang.IncompatibleClassChangeError",
            "java.lang.ArithmeticException",
            "java.lang.ArrayIndexOutOfBoundsException",
            "java.lang.IndexOutOfBoundsException",
            "java.lang.ArrayStoreException", "java.lang.Boolean",
            "java.lang.Byte", "java.lang.Number", "java.lang.Character$Subset",
            "java.lang.Character$UnicodeBlock", "java.lang.Character",
            "java.lang.ClassCastException", "java.lang.ClassCircularityError",
            "java.lang.Compiler", "java.lang.Double",
            "java.lang.ExceptionInInitializerError", "java.lang.Float",
            "java.lang.IllegalAccessError",
            "java.lang.IllegalMonitorStateException",
            "java.lang.IllegalStateException",
            "java.lang.IllegalThreadStateException",
            "java.lang.InheritableThreadLocal", "java.lang.ThreadLocal",
            "java.lang.InstantiationError", "java.lang.Integer",
            "java.lang.InternalError", "java.lang.VirtualMachineError",
            "java.lang.Long", "java.lang.Math",
            "java.lang.NegativeArraySizeException",
            "java.lang.NoClassDefFoundError", "java.lang.Process",
            "java.lang.NoSuchFieldError", "java.lang.NoSuchMethodError",
            "java.lang.NullPointerException", "java.lang.OutOfMemoryError",
            "java.lang.Runnable", "java.lang.Runtime", "java.lang.Thread",
            "java.lang.ThreadGroup", "java.lang.RuntimePermission",
            "java.lang.SecurityManager", "java.lang.Short",
            "java.lang.StackOverflowError", "java.lang.StrictMath",
            "java.lang.StringIndexOutOfBoundsException", "java.lang.System",
            "java.lang.ThreadDeath", "java.lang.UnknownError",
            "java.lang.UnsatisfiedLinkError",
            "java.lang.UnsupportedClassVersionError",
            "java.lang.UnsupportedOperationException", "java.lang.VerifyError",
            "java.lang.Void", "java.math.BigInteger", "java.net.URL",
            "java.net.URLStreamHandlerFactory",
            "java.net.MalformedURLException", "java.net.URLStreamHandler",
            "java.net.URLConnection", "java.net.InetAddress",
            "java.net.FileNameMap", "java.net.ContentHandlerFactory",
            "java.net.UnknownHostException", "java.net.ContentHandler",
            "java.net.Authenticator", "java.net.PasswordAuthentication",
            "java.net.BindException", "java.net.SocketException",
            "java.net.ConnectException", "java.net.DatagramPacket",
            "java.net.DatagramSocket", "java.net.DatagramSocketImplFactory",
            "java.net.DatagramSocketImpl", "java.net.SocketOptions",
            "java.net.HttpURLConnection", "java.net.ProtocolException",
            "java.net.JarURLConnection", "java.net.MulticastSocket",
            "java.net.NetPermission", "java.net.NoRouteToHostException",
            "java.net.ServerSocket", "java.net.Socket", "java.net.SocketImpl",
            "java.net.SocketImplFactory", "java.net.SocketPermission",
            "java.net.URLClassLoader", "java.net.URLDecoder",
            "java.net.URLEncoder", "java.net.UnknownServiceException",
            "java.security.acl.Acl", "java.security.acl.Owner",
            "java.security.acl.AclEntry",
            "java.security.acl.NotOwnerException",
            "java.security.acl.Permission",
            "java.security.acl.LastOwnerException",
            "java.security.acl.AclNotFoundException",
            "java.security.acl.Group",
            "java.security.cert.Certificate$CertificateRep",
            "java.security.cert.Certificate",
            "java.security.cert.CertificateEncodingException",
            "java.security.cert.CertificateException",
            "java.security.cert.CRL", "java.security.cert.CRLException",
            "java.security.cert.CertificateExpiredException",
            "java.security.cert.CertificateFactory",
            "java.security.cert.CertificateFactorySpi",
            "java.security.cert.CertificateNotYetValidException",
            "java.security.cert.CertificateParsingException",
            "java.security.cert.X509CRL", "java.security.cert.X509Extension",
            "java.security.cert.X509CRLEntry",
            "java.security.cert.X509Certificate",
            "java.security.interfaces.DSAKey",
            "java.security.interfaces.DSAParams",
            "java.security.interfaces.DSAKeyPairGenerator",
            "java.security.interfaces.DSAPrivateKey",
            "java.security.interfaces.DSAPublicKey",
            "java.security.interfaces.RSAKey",
            "java.security.interfaces.RSAPrivateCrtKey",
            "java.security.interfaces.RSAPrivateKey",
            "java.security.interfaces.RSAPublicKey",
            "java.security.spec.AlgorithmParameterSpec",
            "java.security.spec.InvalidParameterSpecException",
            "java.security.spec.KeySpec",
            "java.security.spec.InvalidKeySpecException",
            "java.security.spec.DSAParameterSpec",
            "java.security.spec.DSAPrivateKeySpec",
            "java.security.spec.DSAPublicKeySpec",
            "java.security.spec.EncodedKeySpec",
            "java.security.spec.PKCS8EncodedKeySpec",
            "java.security.spec.RSAKeyGenParameterSpec",
            "java.security.spec.RSAPrivateCrtKeySpec",
            "java.security.spec.RSAPrivateKeySpec",
            "java.security.spec.RSAPublicKeySpec",
            "java.security.spec.X509EncodedKeySpec",
            "java.security.ProtectionDomain", "java.security.CodeSource",
            "java.security.PermissionCollection", "java.security.Permission",
            "java.security.Guard", "java.security.GeneralSecurityException",
            "java.security.PublicKey", "java.security.Key",
            "java.security.NoSuchAlgorithmException",
            "java.security.InvalidKeyException", "java.security.KeyException",
            "java.security.NoSuchProviderException",
            "java.security.SignatureException",
            "java.security.BasicPermission", "java.security.SecureClassLoader",
            "java.security.AccessControlContext",
            "java.security.DomainCombiner",
            "java.security.AccessControlException",
            "java.security.AccessController", "java.security.PrivilegedAction",
            "java.security.PrivilegedExceptionAction",
            "java.security.PrivilegedActionException",
            "java.security.AlgorithmParameterGenerator",
            "java.security.AlgorithmParameterGeneratorSpi",
            "java.security.AlgorithmParameters",
            "java.security.AlgorithmParametersSpi", "java.security.Provider",
            "java.security.SecureRandom", "java.security.SecureRandomSpi",
            "java.security.InvalidAlgorithmParameterException",
            "java.security.AllPermission", "java.security.Certificate",
            "java.security.Principal", "java.security.DigestException",
            "java.security.DigestInputStream", "java.security.MessageDigest",
            "java.security.MessageDigestSpi",
            "java.security.DigestOutputStream", "java.security.GuardedObject",
            "java.security.Identity", "java.security.IdentityScope",
            "java.security.KeyManagementException",
            "java.security.InvalidParameterException",
            "java.security.KeyFactory", "java.security.KeyFactorySpi",
            "java.security.PrivateKey", "java.security.KeyPair",
            "java.security.KeyPairGenerator",
            "java.security.KeyPairGeneratorSpi", "java.security.KeyStore",
            "java.security.KeyStoreSpi", "java.security.KeyStoreException",
            "java.security.UnrecoverableKeyException",
            "java.security.Permissions", "java.security.Policy",
            "java.security.ProviderException", "java.security.Security",
            "java.security.SecurityPermission", "java.security.Signature",
            "java.security.SignatureSpi", "java.security.SignedObject",
            "java.security.Signer", "java.security.UnresolvedPermission",
            "java.text.resources.BreakIteratorRules",
            "java.text.resources.BreakIteratorRules_th",
            "java.text.Annotation",
            "java.text.AttributedCharacterIterator$Attribute",
            "java.text.AttributedCharacterIterator",
            "java.text.CharacterIterator", "java.text.AttributedString",
            "java.text.BreakIterator", "java.text.ChoiceFormat",
            "java.text.NumberFormat", "java.text.Format",
            "java.text.FieldPosition", "java.text.ParsePosition",
            "java.text.ParseException", "java.text.CollationElementIterator",
            "java.text.RuleBasedCollator", "java.text.Collator",
            "java.text.CollationKey", "java.text.DateFormat",
            "java.text.DateFormatSymbols", "java.text.DecimalFormat",
            "java.text.DecimalFormatSymbols", "java.text.MessageFormat",
            "java.text.SimpleDateFormat", "java.text.StringCharacterIterator",
            "java.util.jar.Attributes$Name", "java.util.jar.Attributes",
            "java.util.jar.JarEntry", "java.util.jar.Manifest",
            "java.util.jar.JarFile", "java.util.jar.JarException",
            "java.util.jar.JarInputStream", "java.util.jar.JarOutputStream",
            "java.util.zip.ZipEntry", "java.util.zip.ZipConstants",
            "java.util.zip.ZipFile", "java.util.zip.ZipException",
            "java.util.zip.ZipInputStream",
            "java.util.zip.InflaterInputStream", "java.util.zip.Inflater",
            "java.util.zip.DataFormatException",
            "java.util.zip.ZipOutputStream",
            "java.util.zip.DeflaterOutputStream", "java.util.zip.Deflater",
            "java.util.zip.Adler32", "java.util.zip.Checksum",
            "java.util.zip.CRC32", "java.util.zip.CheckedInputStream",
            "java.util.zip.CheckedOutputStream",
            "java.util.zip.GZIPInputStream", "java.util.zip.GZIPOutputStream",
            "java.util.Locale", "java.util.Comparator",
            "java.util.Enumeration", "java.util.MissingResourceException",
            "java.util.Properties", "java.util.Hashtable",
            "java.util.Dictionary", "java.util.Map$Entry", "java.util.Map",
            "java.util.Set", "java.util.Collection", "java.util.Iterator",
            "java.util.Random", "java.util.Date", "java.util.Calendar",
            "java.util.TimeZone", "java.util.ListResourceBundle",
            "java.util.ResourceBundle", "java.util.AbstractCollection",
            "java.util.AbstractList", "java.util.List",
            "java.util.ListIterator", "java.util.AbstractMap",
            "java.util.AbstractSequentialList", "java.util.AbstractSet",
            "java.util.ArrayList", "java.util.Arrays", "java.util.BitSet",
            "java.util.Collections", "java.util.SortedMap",
            "java.util.SortedSet", "java.util.ConcurrentModificationException",
            "java.util.EmptyStackException", "java.util.EventListener",
            "java.util.EventObject", "java.util.GregorianCalendar",
            "java.util.HashMap", "java.util.HashSet", "java.util.LinkedList",
            "java.util.NoSuchElementException", "java.util.Observable",
            "java.util.Observer", "java.util.PropertyPermission",
            "java.util.PropertyResourceBundle", "java.util.SimpleTimeZone",
            "java.util.Stack", "java.util.Vector", "java.util.StringTokenizer",
            "java.util.Timer", "java.util.TimerTask",
            "java.util.TooManyListenersException", "java.util.TreeMap",
            "java.util.TreeSet", "java.util.WeakHashMap",
            "javax.microedition.io.Connection",
            "javax.microedition.io.ConnectionNotFoundException",
            "javax.microedition.io.Connector",
            "javax.microedition.io.ContentConnection",
            "javax.microedition.io.StreamConnection",
            "javax.microedition.io.InputConnection",
            "javax.microedition.io.OutputConnection",
            "javax.microedition.io.Datagram",
            "javax.microedition.io.DatagramConnection",
            "javax.microedition.io.HttpConnection",
            "javax.microedition.io.StreamConnectionNotifier",};

    static Hashtable foundationMap = new Hashtable();

    static Hashtable minimumMap = new Hashtable();

    /**
     * Check if a class is allowed by the "Foundation" profile.
     */
    public static boolean isFoundation(String className)
    {
        return foundationMap.containsKey(className);
    }

    /**
     * Check if a class is allowed by the "Mimimum" profile.
     */
    public static boolean isMinimum(String className)
    {
        return minimumMap.containsKey(className);
    }

    static {
        for (int i = 0; i < EE_FOUNDATION.length; i++) {
            foundationMap.put(EE_FOUNDATION[i], "");
        }

        for (int i = 0; i < EE_MINIMUM.length; i++) {
            minimumMap.put(EE_MINIMUM[i], "");
        }
    }

}