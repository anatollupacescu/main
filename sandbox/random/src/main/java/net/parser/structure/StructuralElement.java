package net.parser.structure;

public enum StructuralElement {
	
	START,			//	COMPLEX_START ~KEY_VALUE, KEY_VALUE_SEQ~ COMPLEX_END

	KEY_VALUE_SEQ,	//	(KEY_VALUE COMMA)* KEY_VALUE
	KEY_VALUE,		//	KEY VALUE

	KEY,			//	STRING COLUMN
	VALUE,			//	~STRING, ARRAY, COMPLEX~

	STRING,			//	QUOTS CHAR* QUOTS
	ARRAY,			//	ARRAY_START	(KEY_VALUE COMMA)* ARRAY_END
	COMPLEX			//	COMPLEX_START (KEY_VALUE COMMA)* COMPLEX_END
}