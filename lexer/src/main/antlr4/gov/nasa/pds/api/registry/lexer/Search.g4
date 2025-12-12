grammar Search;

query : queryTerm EOF ; 
queryTerm : comparison | likeComparison | existence | group ;
group : NOT? LPAREN expression RPAREN ;
existence : ( FIELD | STRINGVAL ) EXISTS ;
expression : andStatement | orStatement | queryTerm ;
andStatement : queryTerm (AND queryTerm)+ ;
orStatement : queryTerm (OR queryTerm)+ ;
comparison : FIELD operator ( NUMBER | STRINGVAL ) ;
likeComparison : FIELD LIKE STRINGVAL ;
operator : EQ | NE | GT | GE | LT | LE ;

NOT : 'NOT' | 'not' ;

EQ : E Q ;
NE : N E ;
GT : G T ;
GE : G E ;
LT : L T ;
LE : L E ;

EXISTS: E X I S T S;
LIKE: L I K E;

LPAREN : '(' ;
RPAREN : ')' ;

AND : A N D ;
OR  : O R ;

FIELD     : [A-Za-z_] [A-Za-z0-9_.:/]* ;
STRINGVAL : '"' ~["\r\n]* '"' ;
NUMBER :  ('-')? [0-9]+ ('.' [0-9]*)?  ;

WS : [ \t\r\n]+ -> skip ;


// case-insensitivity fragments, per https://chromium.googlesource.com/external/github.com/antlr/antlr4/+/2191c386190a7d57d457319dd2f6aec4f0231d4c/doc/case-insensitive-lexing.md
fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];