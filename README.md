# Hotfix
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
