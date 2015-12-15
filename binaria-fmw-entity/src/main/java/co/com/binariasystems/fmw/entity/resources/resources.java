package co.com.binariasystems.fmw.entity.resources;

public final class resources {
	public static String resourcesPackage(){
		return resources.class.getPackage().getName();
	}
	
	public static String resourcesPath(){
		return new StringBuilder("/").append(resourcesPackage().replace(".", "/")).append("/").toString();
	}
	
	public static String imagesPath(){
		return new StringBuilder(resourcesPath()).append("images/").toString();
	}
	
	public static String propertiesPath(){
		return new StringBuilder(resourcesPath()).append("properties/").toString();
	}
	
	public static String messagesPackage(){
		return new StringBuilder(resourcesPackage()).append(".messages").toString();
	}
	
	public static String getImageFilePath(String simpleFileName){
		return imagesPath()+simpleFileName;
	}
	
	public static String getPropertyFilePath(String simpleFileName){
		return propertiesPath()+simpleFileName;
	}
	
	public static String getMessageFilePath(String simpleFileName){
		return messagesPackage()+"."+simpleFileName;
	}
	
	public static String getAbsoluteImageFilePath(String simpleFileName){
		return resources.class.getResource(imagesPath()+simpleFileName).getPath();
	}
	
	public static String getAbsolutePropertyFilePath(String simpleFileName){
		return resources.class.getResource(propertiesPath()+simpleFileName).getPath();
	}
}
