package com.bb.hotfix;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 思路：修改当前应用ClassLoader成员变量pathList（DexPathList）的成员变量dexElements（Element[]）
 * 1、获取当前应用ClassLoader成员变量pathList
 * 2、创建补丁ClassLoader（通过new DexClassLoader（patchDexPath,optimizedDirectory,librarySearchPath,parentClassLoader）），
 *    获取补丁ClassLoader成员变量pathList
 * 3、分别获取1、2中pathList的成员变量dexElements（Element[]）
 * 4、合并3中获取的Element[]，并设置给当前应用ClassLoader成员变量pathList（DexPathList）的成员变量dexElements（Element[]）
 *
 *
 * dex打包：
 * 将修改后的补丁class文件打包成dex目录（编译出来的class文件在app/build/intermediates/javac/debug/classes目录中）
 * 打包命令===》dx --dex --output=输出路径/classes.dex class文件的根目录所在路径（例如：class文件路径C:\Hotfix\com\bb\hotfix\A.class,
 * 根目录所在路径即为：C:\Hotfix\）
 */
public class HotFixUtils {
    static String DIR_HOTFIX = "hotfix";
    private static final String OPTIMIZE_DEX_DIR = "optimize_dex";
    private static File[] patchFiles;

    public static boolean checkShouldFix() {
        boolean shouldFix = false;
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File hotfixDir = new File(externalStorageDirectory, DIR_HOTFIX);
        hotfixDir = externalStorageDirectory;
        if (hotfixDir.exists() && hotfixDir.isDirectory()) {
            patchFiles = hotfixDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("classes");
                }
            });
            return patchFiles.length != 0;
        }
        return false;
    }

    public static void hotfix(Context context) {
        String optimizeDir = context.getFilesDir().getAbsolutePath() +
                File.separator + OPTIMIZE_DEX_DIR;
        // data/data/包名/files/optimize_dex（这个必须是自己程序下的目录）

        File fopt = new File(optimizeDir);
        if (!fopt.exists()) {
            fopt.mkdirs();
        }

        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        for (File patchFile : patchFiles) {
            DexClassLoader dexClassLoader = new DexClassLoader(
                    patchFile.getAbsolutePath(),// 修复好的dex（补丁）所在目录
                    fopt.getAbsolutePath(),// 存放dex的解压目录（用于jar、zip、apk格式的补丁）
                    null,
                    pathClassLoader);

            Object patchPathList = getPathList(dexClassLoader);
            Object originalPathList = getPathList(pathClassLoader);

            Object patchElements = getElements(patchPathList);
            Object originalElements = getElements(originalPathList);

            Object combineArray = combineArray(patchElements, originalElements);//合并elements[],将补丁elements[]放在数组开头（应用加载时从elements[0]开始遍历）

            setElements(getPathList(pathClassLoader), combineArray);//将合并后的elements[]设置给当前应用ClassLoader成员变量pathList（DexPathList）的成员变量dexElements（Element[]）
        }
    }

    private static Object getPathList(Object baseDexClassLoader) {
        return ReflectUtils.getField(baseDexClassLoader, "pathList");
    }

    private static Object getElements(Object DexPathList) {
        return ReflectUtils.getField(DexPathList, "dexElements");
    }

    private static void setElements(Object DexPathList, Object dexElements) {
        ReflectUtils.setField(DexPathList, "dexElements", dexElements);
    }

    /**
     * 数组合并
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);// 得到左数组长度（补丁数组）
        int j = Array.getLength(arrayRhs);// 得到原dex数组长度
        int k = i + j;// 得到总数组长度（补丁数组+原dex数组）
        Object result = Array.newInstance(clazz, k);// 创建一个类型为clazz，长度为k的新数组
        System.arraycopy(arrayLhs, 0, result, 0, i);
        System.arraycopy(arrayRhs, 0, result, i, j);
        return result;
    }
}
