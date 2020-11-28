package com.raylabz.mocha.schema;

public class Schema {

    private final SchemaType schemaType;

    public Schema(SchemaType schemaType) {
        this.schemaType = schemaType;
    }

    public SchemaType getSchemaType() {
        return schemaType;
    }

}
