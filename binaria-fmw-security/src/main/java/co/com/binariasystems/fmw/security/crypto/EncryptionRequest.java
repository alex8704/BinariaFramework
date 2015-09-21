package co.com.binariasystems.fmw.security.crypto;

public class EncryptionRequest {
	//Cadena a encriptar
	private Object source;
	//Cadena arbitraria a usar para
	//Crear hashes menos vulnerables (Debe estar en base64)
	private String privateSalt;
	//Nombre de algoritmo a usar
	private String algorithmName;
	//Numero de iteraciones para el proceso de encriptacion
	private int hashIterations;
	//Si esta en true indica que la salida debe ser en formato Hexadecimal
	//De lo contrario sera en formato base64
	private boolean hexFormat;
	//Indica si se usara un salt aleatorio
	//Para generar hashes diferentes para una misma entrada
	private boolean generatePublicSalt;
	
	public EncryptionRequest() {
	}
	
	public EncryptionRequest(Object source, String privateSalt, String algorithmName, int hashIterations, boolean hexFormat) {
		super();
		this.source = source;
		this.privateSalt = privateSalt;
		this.algorithmName = algorithmName;
		this.hashIterations = hashIterations;
		this.hexFormat = hexFormat;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public String getPrivateSalt() {
		return privateSalt;
	}

	public void setPrivateSalt(String privateSalt) {
		this.privateSalt = privateSalt;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public int getHashIterations() {
		return hashIterations;
	}

	public void setHashIterations(int hashIterations) {
		this.hashIterations = hashIterations;
	}

	public boolean isHexFormat() {
		return hexFormat;
	}

	public void setHexFormat(boolean hexFormat) {
		this.hexFormat = hexFormat;
	}

	public boolean isGeneratePublicSalt() {
		return generatePublicSalt;
	}

	public void setGeneratePublicSalt(boolean generatePublicSalt) {
		this.generatePublicSalt = generatePublicSalt;
	}

		
}
