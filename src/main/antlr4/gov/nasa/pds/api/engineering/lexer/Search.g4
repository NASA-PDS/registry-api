grammar Search;

query : queryTerm EOF ; 
queryTerm : comparison | group ;
group : NOT? LPAREN expression RPAREN ;
expression : andStatement | orStatement | queryTerm ;
andStatement : queryTerm (AND queryTerm)+ ;
orStatement : queryTerm (OR queryTerm)+ ;
comparison : FIELD operator ( NUMBER | STRINGVAL | wildcardFunc ) ;
operator : EQ | NE | GT | GE | LT | LE ;

wildcardFunc : ('WILDCARD' | 'wildcard') LPAREN STRINGVAL RPAREN ;


NOT : 'not' ;

EQ : 'eq' ;
NE : 'ne' ;
GT : 'gt' ;
GE : 'ge' ;
LT : 'lt' ;
LE : 'le' ;

LPAREN : '(' ;
RPAREN : ')' ;

AND : 'AND' | 'and' ;
OR  : 'OR' | 'or' ;

FIELD     : [A-Za-z_] [A-Za-z0-9_.:/]* ;
STRINGVAL : '"' ~["\r\n]* '"' ;
NUMBER :  ('-')? [0-9]+ ('.' [0-9]*)?  ;

WS : [ \t\r\n]+ -> skip ;