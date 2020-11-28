package com.raylabz.mocha.schema;

public class ArraySchema<TSchema extends Schema> extends Schema {

    private final TSchema[] elementSchemas;

    public ArraySchema(TSchema[] elementSchemas) {
        super(SchemaType.ARRAY);
        this.elementSchemas = elementSchemas;
    }

    public TSchema[] getElementSchemas() {
        return elementSchemas;
    }

}
