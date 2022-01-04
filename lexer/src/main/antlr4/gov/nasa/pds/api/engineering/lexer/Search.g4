grammar Search;

query : queryTerm EOF ; 
queryTerm : comparison | likeComparison | group ;
group : NOT? LPAREN expression RPAREN ;
expression : andStatement | orStatement | queryTerm ;
andStatement : queryTerm (AND queryTerm)+ ;
orStatement : queryTerm (OR queryTerm)+ ;
comparison : FIELD operator ( NUMBER | STRINGVAL ) ;
likeComparison : FIELD NOT? LIKE STRINGVAL ;
operator : EQ | NE | GT | GE | LT | LE ;

NOT : 'not' ;

EQ : 'eq' ;
NE : 'ne' ;
GT : 'gt' ;
GE : 'ge' ;
LT : 'lt' ;
LE : 'le' ;

LIKE: 'like';

LPAREN : '(' ;
RPAREN : ')' ;

AND : 'AND' | 'and' ;
OR  : 'OR' | 'or' ;

FIELD     : [A-Za-z_] [A-Za-z0-9_.:/]* ;
STRINGVAL : '"' ~["\r\n]* '"' ;
NUMBER :  ('-')? [0-9]+ ('.' [0-9]*)?  ;

WS : [ \t\r\n]+ -> skip ;
