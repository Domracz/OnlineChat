package net.domraczpvp.java.onlinechat.server;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class API {
    static final List<Mod> mods = new ArrayList<>();

    public void kickUser(Client client, String reason) {
        try {
            Server.sendPrivateMessage(reason, client);
            Server.setDisconnectReason(client, reason);
            Server.closeConnection(client);
        }catch (Exception ignored) {

        }
    }

    public void banUser(Client client, String reason) {
        try {
            Server.sendPrivateMessage(reason, client);
            Server.setDisconnectReason(client, reason);
            BanManager.banUser(client.getConnection().getInetAddress().getHostAddress(), reason);
            Server.closeConnection(client);
        }catch (Exception ignored) {

        }
    }

    // Usage example
    public static boolean command(Client client, String command, String[] args) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("commandExecuted", Client.class, String.class, String[].class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    boolean result = (boolean) method.invoke(instance, client, command, args);
                    if (result) {
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean message(Client client, String message) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("sendMessage", Client.class, String.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    boolean result = (boolean) method.invoke(instance, client, message);
                    if (result) {
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static UsernameResponse username(Client client, String un) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("onUsername", Client.class, String.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    UsernameResponse result = (UsernameResponse) method.invoke(instance, client, un);
                    if (result != null) {
                        if (result.redo) {
                            return result;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new UsernameResponse(false, null);
    }

    public static void join(Client client) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("onJoin", Client.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    method.invoke(instance, client);
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void leave(Client client) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("onLeave", Client.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    method.invoke(instance, client);
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void connect(Client client) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("onConnect", Client.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    method.invoke(instance, client);
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void registerMods() {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("getInstance");
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Mod result = (Mod) method.invoke(instance);
                    Method method1 = clazz.getMethod("onLoad");
                    Object instance1 = clazz.getDeclaredConstructor().newInstance();
                    mods.add(result);
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean packet(Client client, String packet, String data) {
        String packageName = "net.domraczpvp.java.onlinechat.server.mods"; // Specify your package name
        Class<?> superClass = Mod.class; // Specify your superclass

        try {
            List<Class<?>> classes = getClassesInPackage(packageName, superClass);

            // Invoke the method in each class with parameters
            for (Class<?> clazz : classes) {
                try {
                    Method method = clazz.getMethod("onPacket", Client.class, String.class, String.class);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    boolean result = (boolean) method.invoke(instance, client, packet, data);
                    if (result) {
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    // Handle if the method is not found in the class
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    // Handle any other exceptions that may occur during method invocation
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Class<?>> getClassesInPackage(String packageName, Class<?> superClass)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());

            if (directory.exists()) {
                String[] files = directory.list();
                if (files != null) {
                    for (String file : files) {
                        if (file.endsWith(".class")) {
                            String className = packageName + '.' + file.substring(0, file.length() - 6);
                            Class<?> clazz = Class.forName(className);

                            if (superClass.isAssignableFrom(clazz)) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }

        return classes;
    }

}
