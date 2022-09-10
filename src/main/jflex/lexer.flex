package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.constants.Constants;
import lyc.compiler.model.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
	private boolean verificarLongitudID() {
		return yylength() > Constants.MAX_ID_LENGTH;
	}
	private boolean verificarLongitudString() {
		return yylength() > Constants.MAX_STRING_LITERAL_LENGTH;
	}
%}

LineTerminator 		= 	\r|\n|\r\n
InputCharacter 		= 	[^\r\n]
Identation 			=  	[ \t\f]

DIGITO				=	[0-9]
LETRA				=	[a-zA-Z]

NUMERAL				=   "#"
INCLUDE				=	"include"
PUNTO           	=   "."
INTEGER				=	"int"
FLOAT				=	"float"
CHAR				=	"char"
MAIN				=	"main"
ELSE				=	"else"
CORCH_ABRE			=	"["
CORCH_CIERRA		=	"]"
ASIGNACION			=	"="
PUNTO_COMA			=	";"
SYS_OUT				=	"printf"
SYS_IN				=	"scanf"
AND					=	"&&"
OR					=	"||"
POINTER				=	"&"
BARRA				=	"\/"
ASTERISCO			=	"\*"
SALTO_LINEA			=	"\n|\r\n|\r"
COMENTARIO_MULTI    =   "\/\*((\n|\r\n|\r)*([^*]*)(\n|\r\n|\r)*)*\*\/"
COMENTARIO_SINGLE   =   "\/\/.*"

CTE		            =	{DIGITO}+
ID			        =	{LETRA}({LETRA}|{DIGITO})*

OP_SUM		        =	"+"
OP_MUL              =	"*"
OP_RES		        =	"-"
OP_DIV              =	"/"
PA			        =	"("
PC			        =	")"
IF                  =	"if"
WHILE               =	"while"
COMA                =	","
TEXTO               =	[\"].*[\"]
DISTINTO            =	"!="
IGUAL_IGUAL         =	"=="
MENOR_IGUAL         =	"<="
MAYOR_IGUAL         =	">="
MENOR               =	"<"
MAYOR               =	">"
PARENTESIS_ABRE     =	"{"
PARENTESIS_CIERRA   =	"}"
CTE_REAL            =	[0]{0,1}[1-9]{0,11}[.][0-9]{0,11}

%%
/* keywords */

<YYINITIAL> {

{NUMERAL}		    {	return symbol(ParserSym.NUMERAL,yytext()); 		}
{INCLUDE}		    {	return symbol(ParserSym.INCLUDE,yytext()); 		}
{PUNTO}		        {	return symbol(ParserSym.PUNTO,yytext()); 		}
{MAIN}		        {	return symbol(ParserSym.MAIN,yytext());			}
{CHAR}		        {	return symbol(ParserSym.CHAR,yytext());			}
{CORCH_ABRE}		{	return symbol(ParserSym.CORCH_ABRE,yytext());	}
{CORCH_CIERRA}		{	return symbol(ParserSym.CORCH_CIERRA,yytext());	}
{ASIGNACION}		{	return symbol(ParserSym.ASIGNACION,yytext());	}
{PUNTO_COMA}	    {	return symbol(ParserSym.PUNTO_COMA,yytext());	}
{SYS_OUT}	        {	return symbol(ParserSym.SYS_OUT,yytext());		}
{SYS_IN}	        {	return symbol(ParserSym.SYS_IN,yytext());		}
{AND}	            {	return symbol(ParserSym.AND,yytext());			}
{OR}	            {	return symbol(ParserSym.OR,yytext());			}
{POINTER}	        {	return symbol(ParserSym.POINTER,yytext());		}
{ELSE}	            {	return symbol(ParserSym.ELSE,yytext());		}
{COMENTARIO_SINGLE} {	/*ignorar*/										}
{COMENTARIO_MULTI}  {	/*ignorar*/										}

{INTEGER}		    {	return symbol(ParserSym.INTEGER, yytext());		}
{FLOAT}		        {	return symbol(ParserSym.FLOAT, yytext());		}
{IF}                {	return symbol(ParserSym.IF, yytext());			}
{WHILE}             {	return symbol(ParserSym.WHILE, yytext());		}
{COMA}	            {	return symbol(ParserSym.COMA, yytext());		}
{CTE}			    {	return symbol(ParserSym.CTE, yytext());			}
{TEXTO}			    {
      					if(verificarLongitudString())
      						throw new InvalidLengthException("\"" + yytext() + "\""+ " excede el maximo permitido");
						return symbol(ParserSym.TEXTO, yytext());
	  				}
{ID}			    {
      					if(verificarLongitudID())
      						throw new InvalidLengthException("\"" + yytext() + "\""+ " excede el maximo permitido");
		  				return symbol(ParserSym.ID, yytext());
	  				}

{MENOR_IGUAL}	    {	return symbol(ParserSym.MENOR_IGUAL, yytext());	}
{MAYOR_IGUAL}	    {	return symbol(ParserSym.MAYOR_IGUAL, yytext());	}
{MENOR}			    {	return symbol(ParserSym.MENOR, yytext());			}
{MAYOR}			    {	return symbol(ParserSym.MAYOR, yytext());			}
{PARENTESIS_ABRE}	{	return symbol(ParserSym.PARENTESIS_ABRE, yytext());	}
{PARENTESIS_CIERRA}	{	return symbol(ParserSym.PARENTESIS_CIERRA, yytext());	}
{CTE_REAL}	        {	return symbol(ParserSym.CTE_REAL, yytext());		}
{DISTINTO}	        {	return symbol(ParserSym.DISTINTO, yytext());		}
{IGUAL_IGUAL}	    {	return symbol(ParserSym.IGUAL_IGUAL, yytext());		}
{OP_SUM}			{	return symbol(ParserSym.OP_SUM, yytext());		}
{OP_MUL}			{	return symbol(ParserSym.OP_MUL, yytext());		}
{OP_RES}			{	return symbol(ParserSym.OP_RES, yytext());		}
{OP_DIV}			{	return symbol(ParserSym.OP_DIV, yytext());		}
{PA}				{	return symbol(ParserSym.PA, yytext());			}
{PC}				{	return symbol(ParserSym.PC, yytext());			}
"\n"				{}
"\t"				{}
"\n\t"				{}
" "					{}
"\r\n"				{}
}


/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
