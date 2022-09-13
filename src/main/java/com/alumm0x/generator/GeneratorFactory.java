package com.alumm0x.generator;

import burp.IIntruderAttack;
import burp.IIntruderPayloadGenerator;
import burp.IIntruderPayloadGeneratorFactory;

public class GeneratorFactory implements IIntruderPayloadGeneratorFactory {
    @Override
    public String getGeneratorName() {
        return "Password";
    }

    @Override
    public IIntruderPayloadGenerator createNewInstance(IIntruderAttack attack) {
        return new PasswordGenerator();
    }
}
