package pl.alpheratzteam.obfuscator.core.transformer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import pl.alpheratzteam.obfuscator.api.transformer.Transformer;
import pl.alpheratzteam.obfuscator.api.util.AccessUtil;
import pl.alpheratzteam.obfuscator.api.util.RandomUtil;

import java.util.Map;

/**
 * @author Unix on 01.09.2019.
 */
public class AntiDebugTransformer implements Transformer {

    private final String[] debugTypes;

    public AntiDebugTransformer() {
        this.debugTypes = new String[]{
                "-Xbootclasspath", "-Xdebug",
                "-agentlib", "-javaagent:",
                "-Xrunjdwp:", "-verbose"
        };
    }

    @Override
    public void transform(@NotNull Map<String, ClassNode> classMap) {
        classMap.values()
                .stream()
                .filter(classNode -> !AccessUtil.isInterface(classNode.access))
                .forEach(classNode -> {
                    if (RandomUtil.nextBoolean()) {
                        return;
                    }

                    final MethodNode x = this.createMethod(classNode);
                    classNode.methods.add(x);

                    for (MethodNode methodNode : classNode.methods) {
                        if (methodNode.name.equals("checkDebug")) {
                            return;
                        }

                        methodNode.instructions.insertBefore(methodNode.instructions.iterator().next().getNext(), new MethodInsnNode(INVOKESTATIC, classNode.name, x.name, x.desc, false));
                        break;
                    }
                });
    }

    @Override
    public String getName() {
        return "AntiDebug";
    }

    @NotNull
    private MethodNode createMethod(@NotNull ClassNode classNode) {
        final MethodNode methodNode = new MethodNode(ACC_PRIVATE | ACC_STATIC, "checkDebug", "()V", null, null);
        methodNode.visitCode();

        final Label label0 = new Label();
        methodNode.visitLabel(label0);
        methodNode.visitLineNumber(11, label0);
        methodNode.visitMethodInsn(INVOKESTATIC, "java/lang/management/ManagementFactory", "getRuntimeMXBean", "()Ljava/lang/management/RuntimeMXBean;", false);
        methodNode.visitMethodInsn(INVOKEINTERFACE, "java/lang/management/RuntimeMXBean", "getInputArguments", "()Ljava/util/List;", true);
        methodNode.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
        methodNode.visitVarInsn(ASTORE, 1);

        final Label label1 = new Label();
        methodNode.visitLabel(label1);
        methodNode.visitFrame(F_APPEND, 1, new Object[]{"java/util/Iterator"}, 0, null);
        methodNode.visitVarInsn(ALOAD, 1);
        methodNode.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);

        final Label label2 = new Label();
        methodNode.visitJumpInsn(IFEQ, label2);
        methodNode.visitVarInsn(ALOAD, 1);
        methodNode.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
        methodNode.visitTypeInsn(CHECKCAST, "java/lang/String");
        methodNode.visitVarInsn(ASTORE, 2);

        final Label label3 = new Label();
        methodNode.visitLabel(label3);
        methodNode.visitLineNumber(12, label3);
        methodNode.visitVarInsn(ALOAD, 2);

        final String debugType = debugTypes[RandomUtil.nextInt(debugTypes.length)];
        methodNode.visitLdcInsn(debugType == null ? debugTypes[0] : debugType);
        methodNode.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);

        final Label label4 = new Label();
        methodNode.visitJumpInsn(IFEQ, label4);

        final Label label5 = new Label();
        methodNode.visitLabel(label5);
        methodNode.visitLineNumber(13, label5);
        methodNode.visitIntInsn(SIPUSH, 666);
        methodNode.visitMethodInsn(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false);
        methodNode.visitLabel(label4);
        methodNode.visitLineNumber(15, label4);
        methodNode.visitFrame(F_SAME, 0, null, 0, null);
        methodNode.visitJumpInsn(GOTO, label1);
        methodNode.visitLabel(label2);
        methodNode.visitLineNumber(16, label2);
        methodNode.visitFrame(F_CHOP, 1, null, 0, null);
        methodNode.visitInsn(RETURN);

        final Label label6 = new Label();
        methodNode.visitLabel(label6);
        methodNode.visitLocalVariable("string", "Ljava/lang/String;", null, label3, label4, 2);
        methodNode.visitLocalVariable("this", "L" + classNode.name + ";", null, label0, label6, 0);

        methodNode.visitMaxs(2, 3);
        methodNode.visitEnd();

        return methodNode;
    }
}