package com.pingan.eauthsdk.util;



import android.content.Context;

public class GetResourceUtil {

	private static String USER_PACKAGE_NAME="";

    public static void setUserPackageName(String packageName) {
            USER_PACKAGE_NAME=packageName;
    }
    public static String getUserPackageName(Context context) {
        if (!USER_PACKAGE_NAME.equals("")) {
            return USER_PACKAGE_NAME;
        } else {
            return   context.getPackageName();
        }
    }

   
    public static int getResourceByType(Context context,String type, String name) {
		return getIdByName(context,
				type, name);
		
	}
	
	@SuppressWarnings("rawtypes")
	public static int getIdByName(Context context, String className, String name) {
//		String packageName = context.getPackageName();
		String packageName = getUserPackageName(context);
		Class r =null;
		int id = 0;
		try {
			r = Class.forName(packageName + ".R");

		
			Class[] classes = r.getClasses();
			if(classes==null){
				return 0;
			}
			Class desireClass = null;

			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}
			if (desireClass != null)
				id = desireClass.getField(name).getInt(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return id;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static int[] getIdsByName(Context context, String className,
			String name) {
		String packageName = getUserPackageName(context);
		
		Class r = null;
		int[] ids = null;
		try {
			r = Class.forName(packageName + ".R");
			
			
			Class[] classes = r.getClasses();
		
			Class desireClass = null;

			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}

			if ((desireClass != null)
					&& (desireClass.getField(name).get(desireClass) != null)
					&& (desireClass.getField(name).get(desireClass).getClass()
							.isArray()))
				ids = (int[]) desireClass.getField(name).get(desireClass);//成员变量的数据类型
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return ids;
	}

}
