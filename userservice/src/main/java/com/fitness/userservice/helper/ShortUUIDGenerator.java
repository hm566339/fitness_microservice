// package com.fitness.userservice.helper;

// import org.hibernate.engine.spi.SharedSessionContractImplementor;
// import org.hibernate.id.IdentifierGenerator;

// import java.io.Serializable;
// import java.util.UUID;

// public class ShortUUIDGenerator implements IdentifierGenerator {

// @Override
// public Serializable generate(SharedSessionContractImplementor session, Object
// object) {
// // Generate an 8-character random ID
// return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
// }
// }
