import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.AuthProvider;
import java.security.Security;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.X509Certificate;
import java.security.ProviderException;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.security.pkcs11.SunPKCS11;

public class sign {

	public static void main(String[] args) throws Exception {

		// Capturando argumentos enviados pelo bash
		// Conteúdo XML
		String xml 			= args[0];
		// Path do certificado
		String certPath 	= args[1];
		// Senha do certificado
		String senha 		= args[2];
		// Nome do XML ao ser assinado
		String localizador 	= args[3];

		// Variáveis para assinatura do XML
		Provider provider 		= null;
		String tag 				= "InfDeclaracaoPrestacaoServico";
		Integer elementIndex 	= 0;
		String alias;
		KeyStore ks;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document docs;
		NodeList elements;
		Element el;
		String id;
		String providerName;
		XMLSignatureFactory fac;
		ArrayList<Object> transformList;
		TransformParameterSpec tps;
		Transform envelopedTransform;
		Transform c14n;
		Reference ref;
		SignedInfo si;
		PrivateKeyEntry keyEntry;
		X509Certificate cert;
		KeyInfoFactory kif;
		ArrayList<Object> x509Content;
		X509Data xd;
		KeyInfo ki;
		DocumentBuilderFactory dbf;
		DOMSignContext dsc;
		XMLSignature signature;
		ByteArrayOutputStream os;
		TransformerFactory tf;
		Transformer trans;

		Collection<String> drivers = new ArrayList();
		drivers.add("/usr/lib/libeTPkcs11.so");
		drivers.add("/usr/lib64/libeTPkcs11.so");
		drivers.add("/usr/lib/libeToken.so");
		drivers.add("/usr/lib/libeToken.so.4");
		drivers.add("/usr/lib/libaetpkss.so");
		drivers.add("/usr/lib/libgpkcs11.so");
		drivers.add("/usr/lib/libgpkcs11.so.2");
		drivers.add("/usr/lib/libepsng_p11.so");
		drivers.add("/usr/lib/libepsng_p11.so.1");
		drivers.add("/usr/local/ngsrv/libepsng_p11.so.1");
		drivers.add("/usr/lib/libcmP11.so");
		drivers.add("/usr/lib/libwdpkcs.so");
		drivers.add("/usr/local/lib64/libwdpkcs.so");
		drivers.add("/usr/local/lib/libwdpkcs.so");
		drivers.add("/usr/lib/watchdata/ICP/lib/libwdpkcs_icp.so");
		drivers.add("/usr/lib/watchdata/lib/libwdpkcs.so");
		drivers.add("/opt/watchdata/lib64/libwdpkcs.so");
		drivers.add("/usr/lib/opensc-pkcs11.so");
		drivers.add("/usr/lib/pkcs11/opensc-pkcs11.so");
		drivers.add("/usr/lib/libwdpkcs.dylib");
		drivers.add("/usr/local/lib/libwdpkcs.dylib");
		drivers.add("/usr/local/ngsrv/libepsng_p11.so.1.2.2");

		Iterator i$ = drivers.iterator();

		while(i$.hasNext()) {
			String s = (String)i$.next();
			File driverRead = new File(s);
			if (driverRead.exists()) {
				try {
					provider = new SunPKCS11(new ByteArrayInputStream((new String("name = SafeWeb\nlibrary =  " + driverRead.getAbsolutePath() + "\n" + "showInfo = true")).getBytes()));
					AuthProvider ap = (AuthProvider)provider;
					ap.logout();
					Security.addProvider(provider);
					break;
				} 
				catch (ProviderException var8) {
					System.out.print("Erro ao capturar driver para assinatura do XML: " + var8 + "\n");
					return;
				}
			}
		}

		try {
			ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(certPath), senha.toCharArray());
		}
		catch (Exception e) {
			System.out.print("Erro ao tentar carregar certificado: " + e + "\n");
			return;
		}

		try {
			alias = (String)ks.aliases().nextElement();
		} 
		catch(Exception e) {
			System.out.print("Erro ao tentar carregar alias do certificado: " + e + "\n");
			return;
		}


		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar DocumentBuilderFactory: " + e + "\n");
			return;
		}


		try {
			builder 	= factory.newDocumentBuilder();
			docs 		= builder.parse(new ByteArrayInputStream(xml.getBytes()));
			elements 	= docs.getElementsByTagName(tag);
			el 			= (Element)elements.item(elementIndex);
			id 			= el.getAttribute("Id");
			/**
				Fix para um throw: Cannot resolve element with ID "id"  
				https://www.javac.com.br/jc/posts/list/1580-erro-ao-assinar-nfe-cannot-resolve-element-with-id.page#top
			*/
			el.setIdAttribute("Id", true);  
		} 
		catch(Exception e) {
			System.out.print("Erro ao recuperar elemento a ser assinado no XML: " + e + "\n");
			return;
		}


		try {
			providerName 	= System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
			fac 			= XMLSignatureFactory.getInstance("DOM", (Provider)Class.forName(providerName).newInstance());
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar XMLSignatureFactory: " + e + "\n");
			return;
		}


		try {
			transformList 		= new ArrayList();
			tps 				= null;
			envelopedTransform 	= fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec)tps);
			transformList.add(envelopedTransform);
			c14n 				= fac.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec)tps);
			transformList.add(c14n);
		} 
		catch(Exception e) {
			System.out.print("Erro ao adicionar método de canonização: " + e + "\n");
			return;
		}


		try {
			ref = fac.newReference("#" + id, fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", (DigestMethodParameterSpec)null), transformList, (String)null, (String)null);
			si 	= fac.newSignedInfo(fac.newCanonicalizationMethod("http://www.w3.org/2001/10/xml-exc-c14n#", (C14NMethodParameterSpec)null), fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", (SignatureMethodParameterSpec)null), Collections.singletonList(ref));
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar DigestMethod e SignatureMethod: " + e + "\n");
			return;
		}


		try {
			keyEntry 	= (PrivateKeyEntry)ks.getEntry(alias, new PasswordProtection(senha.toCharArray()));
			cert 		= (X509Certificate)keyEntry.getCertificate();
		} 
		catch(Exception e) {
			System.out.print("Erro ao recuperar X509Certificate: " + e + "\n");
			return;
		}


		try {
			kif 			= fac.getKeyInfoFactory();
			x509Content 	= new ArrayList();
			x509Content.add(cert);
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar tag X509Content: " + e + "\n");
			return;
		}


		try {
			xd 	= kif.newX509Data(x509Content);
			ki 	= kif.newKeyInfo(Collections.singletonList(xd));
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar tags X509Data e KeyInfo: " + e + "\n");
			return;
		}

		
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar DocumentBuilderFactory[2]: " + e + "\n");
			return;
		}


		try {
			dsc 		= new DOMSignContext(keyEntry.getPrivateKey(), el.getParentNode());
			signature 	= fac.newXMLSignature(si, ki);
			signature.sign(dsc);
		} 
		catch(Exception e) {
			System.out.print("Erro ao criar assinatura: " + e + "\n");
			return;
		}


		try {
			os 		= new ByteArrayOutputStream();
			tf 		= TransformerFactory.newInstance();
			trans 	= tf.newTransformer();
			trans.transform(new DOMSource(docs), new StreamResult(os));
		} 
		catch(Exception e) {
			System.out.print("Erro ao tentar Transform.transform: " + e + "\n");
			return;
		}


		String xmlRetorno 	= os.toString().substring(54);
		File signedXml 		= new File(localizador);
		signedXml.getParentFile().mkdirs();

		try (FileOutputStream fos = new FileOutputStream(signedXml)) {
            
            byte[] mybytes = xmlRetorno.getBytes();
            
            fos.write(mybytes);
        }

	}

}