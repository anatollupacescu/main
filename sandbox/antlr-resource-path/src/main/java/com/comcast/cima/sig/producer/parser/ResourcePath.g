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
  URL_PART+
;

SLASH: '/';

CHAR_ARRAY: ('a'..'z')+;

UP_CHAR_ARRAY: ('A'..'Z')+ CHAR_ARRAY;

GUID: '(\'' CHAR_ARRAY '\')';

URL_PART: SLASH (UP_CHAR_ARRAY | CHAR_ARRAY) GUID?;
