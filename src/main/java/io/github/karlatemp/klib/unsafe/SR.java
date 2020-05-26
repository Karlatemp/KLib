/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/26 18:34:28
 *
 * KLib/KLib.main/SR.java
 */

package io.github.karlatemp.klib.unsafe;

import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

class SR {
    interface Wrapper {
        Class<?> defineClass(Object unsafe, String name, byte[] class_file, int off, int len, ClassLoader classLoader, ProtectionDomain domain);
    }

    static Wrapper defineClass, defineClass0;
    private static final Method defineClass0_met;

    static {
        try {
            (defineClass0_met =
                    ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class))
                    .setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Wrapper load(byte[] code) {
        try {
            return ((Class<?>) defineClass0_met.invoke(Wrapper.class.getClassLoader(), code, 0, code.length))
                    .asSubclass(Wrapper.class)
                    .getConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static Object init() {
        return "";
    }

    static {
        byte[] clazz1 = WrappedClassImplements.createMethodWrapper(
                Type.getInternalName(Wrapper.class),
                "defineClass",
                "(Ljava/lang/Object;Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class;",
                WrappedClassImplements.genImplementClassName("cn.mcres.karlatemp.unsafe", null),
                "sun/misc/Unsafe",
                "defineClass", WrappedClassImplements.MethodWrapperType.OBJECT
        ).toByteArray(), clazz2 = WrappedClassImplements.createMethodWrapper(
                Type.getInternalName(Wrapper.class),
                "defineClass",
                "(Ljava/lang/Object;Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class;",
                WrappedClassImplements.genImplementClassName("cn.mcres.karlatemp.unsafe", null),
                "sun/misc/Unsafe",
                "defineClass0", WrappedClassImplements.MethodWrapperType.OBJECT
        ).toByteArray();
        defineClass = load(clazz1);
        defineClass0 = load(clazz2);
    }
}
