grammar ResourcePath;

options {
  language = Java;
}

@header {
  package com.comcast.cima.sig.producer.parser;
}

@lexer::header {
  package com.comcast.cima.sig.producer.parser;
}

rule: 
  (URL_PART)+
;

SLASH: '/';

QUOTE: '\'';

CHAR_ARRAY: ('a'..'z')+;

UP_CHAR_ARRAY: ('A'..'Z')+ CHAR_ARRAY;

STRING: CHAR_ARRAY | UP_CHAR_ARRAY;

KEY_NAME: STRING;

KEY_PREDICATE: STRING | QUOTE STRING QUOTE;

NAME_PREDICATE: KEY_NAME '=' KEY_PREDICATE;

SINGLE_GUID: KEY_PREDICATE | NAME_PREDICATE;

MULTI_GUID: NAME_PREDICATE (',' NAME_PREDICATE)+;

GUIDS: SINGLE_GUID | MULTI_GUID;

GUID : '(' GUIDS ')';

URL_PART: SLASH (UP_CHAR_ARRAY | CHAR_ARRAY) GUID?;
