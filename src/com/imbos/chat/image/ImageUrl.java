package com.imbos.chat.image;

public class ImageUrl {
	public static String toFileName(String url)
	{
		return url.replaceAll("/","-").replace(":","");
   	}
}
