package lyc.compiler;

import lyc.compiler.factories.LexerFactory;
import lyc.compiler.model.CompilerException;
import lyc.compiler.model.InvalidIntegerException;
import lyc.compiler.model.InvalidLengthException;
import lyc.compiler.model.UnknownCharacterException;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.constants.Constants.MAX_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Disabled
public class LexerTest {

  private Lexer lexer;


  @Test
  public void singleComment() throws Exception{
    scan("//hola");
    int token = nextToken();
    assertThat(nextToken()).isEqualTo(ParserSym.EOF);
  }

  @Test
  public void multiLineComment() throws Exception{
    scan("/*\r\n\rprobando comentario \r\n\n\n*/");
    int token = nextToken();
    assertThat(nextToken()).isEqualTo(ParserSym.EOF);
  }

  @Test
  public void realConstant() throws Exception{
    scan(".01");
    assertThat(nextToken()).isEqualTo(ParserSym.CTE_REAL);
  }

  @Test
  public void ifCase() throws Exception{
    scan("if (a > b && c > b){printf(\"a > b y c > b\");}");
    assertThat(nextToken()).isEqualTo(ParserSym.IF);
    assertThat(nextToken()).isEqualTo(ParserSym.PA);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.MAYOR);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.AND);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.MAYOR);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.PC);
    assertThat(nextToken()).isEqualTo(ParserSym.LLA);
    assertThat(nextToken()).isEqualTo(ParserSym.SYS_OUT);
    assertThat(nextToken()).isEqualTo(ParserSym.PA);
    assertThat(nextToken()).isEqualTo(ParserSym.TEXTO);
    assertThat(nextToken()).isEqualTo(ParserSym.PC);
    assertThat(nextToken()).isEqualTo(ParserSym.PUNTO_COMA);
    assertThat(nextToken()).isEqualTo(ParserSym.LLC);
  }

  @Test
  public void invalidStringConstantLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan("\"%s\"".formatted(getRandomString()));
      nextToken();
    });
  }

  @Test
  public void write() throws Exception{
    scan("printf(\"ewr\");");
    assertThat(nextToken()).isEqualTo(ParserSym.SYS_OUT);
  }
  
  @Test
  public void initToken() throws Exception{
    scan("init");
    assertThat(nextToken()).isEqualTo(ParserSym.INIT);
  }
  @Test
  public void allEqualToken() throws Exception{
    scan("AllEqual");
    assertThat(nextToken()).isEqualTo(ParserSym.ALL_EQUAL);
  }

  @Test
  public void doToken() throws Exception{
    scan("do");
    assertThat(nextToken()).isEqualTo(ParserSym.DO);
  }

  @Test
  public void invalidIdLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan(getRandomString());
      nextToken();
    });
  }

  @Test
  public void invalidPositiveIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(333000));
      int token = nextToken();
    });
  }

  @Test
  public void invalidNegativeIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(-333000));
      nextToken();
    });
  }

  @Test
  public void invalidMinFloatConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("0.000000000000000000000000000000000000001");
      nextToken();
    });
  }

  @Test
  public void invalidMaxFloatConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("1000000000000000000000000000000000000001.0");
      nextToken();
    });
  }

  @Test
  public void assignmentWithExpressions() throws Exception {
    scan("c=d*(e- 21)/4");
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.ASIGNACION);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_MUL);
    assertThat(nextToken()).isEqualTo(ParserSym.PA);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_RES);
    assertThat(nextToken()).isEqualTo(ParserSym.CTE);
    assertThat(nextToken()).isEqualTo(ParserSym.PC);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_DIV);
    assertThat(nextToken()).isEqualTo(ParserSym.CTE);
    assertThat(nextToken()).isEqualTo(ParserSym.EOF);
  }

  @Test
  public void unknownCharacter() {
    assertThrows(UnknownCharacterException.class, () -> {
      scan("@");
      nextToken();
    });
  }

  @AfterEach
  public void resetLexer() {
    lexer = null;
  }

  private void scan(String input) {
    lexer = LexerFactory.create(input);
  }

  private int nextToken() throws IOException, CompilerException {
    return lexer.next_token().sym;
  }

  private static String getRandomString() {
    return new RandomStringGenerator.Builder()
            .filteredBy(CharacterPredicates.LETTERS)
            .withinRange('a', 'z')
            .build().generate(MAX_LENGTH * 2);
  }

}
