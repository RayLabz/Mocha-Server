package com.raylabz.mocha.schema;

import java.util.ArrayList;

public class ObjectSchema extends Schema {

    private final Schema[] innerSchemas;

    private ObjectSchema(Schema[] innerSchemas) {
        super(SchemaType.OBJECT);
        this.innerSchemas = innerSchemas;
    }

    public Schema[] getInnerSchemas() {
        return innerSchemas;
    }

    public static class Builder {

        private final ArrayList<Schema> schemaArrayList = new ArrayList<>();

        public Builder() { }

        public Builder expectData(SchemaType schemaType) {
            Schema schema;
            switch (schemaType) {
                case SHORT:
                    schema = new ShortSchema();
                    break;
                case INT:
                    schema = new IntSchema();
                    break;
                case LONG:
                    schema = new LongSchema();
                    break;
                case FLOAT:
                    schema = new FloatSchema();
                    break;
                case DOUBLE:
                    schema = new DoubleSchema();
                    break;
                case CHAR:
                    schema = new CharacterSchema();
                    break;
                case BOOLEAN:
                    schema = new BooleanSchema();
                    break;
                case BYTE:
                    schema = new ByteSchema();
                    break;
                case UTF8:
                    schema = new UTF8Schema();
                    break;
                default:
                    throw new IllegalArgumentException("Expected basic data type schema but got '" + schemaType + "'.");
            }
            schemaArrayList.add(schema);
            return this;
        }

        public Builder expectObject(ObjectSchema objectSchema) {
            schemaArrayList.add(objectSchema);
            return this;
        }

        public Builder expectArray(ArraySchema<?> arraySchema) {
            schemaArrayList.add(arraySchema);
            return this;
        }

        public ObjectSchema build() {
            Schema[] schemaArray = new Schema[schemaArrayList.size()];
            schemaArrayList.toArray(schemaArray);
            return new ObjectSchema(schemaArray);
        }

    }

}
