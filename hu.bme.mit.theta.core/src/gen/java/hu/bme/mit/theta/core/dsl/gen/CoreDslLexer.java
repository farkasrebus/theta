// Generated from CoreDsl.g4 by ANTLR 4.5.3
package hu.bme.mit.theta.core.dsl.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoreDslLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BOOLTYPE=1, INTTYPE=2, RATTYPE=3, IF=4, THEN=5, ELSE=6, IFF=7, IMPLY=8, 
		FORALL=9, EXISTS=10, OR=11, AND=12, NOT=13, EQ=14, NEQ=15, LT=16, LEQ=17, 
		GT=18, GEQ=19, PLUS=20, MINUS=21, MUL=22, DIV=23, MOD=24, REM=25, PERCENT=26, 
		PRIME=27, TRUE=28, FALSE=29, ASSIGN=30, HAVOC=31, ASSUME=32, INT=33, NAT=34, 
		SIGN=35, DOT=36, ID=37, UNDERSCORE=38, DIGIT=39, LETTER=40, LPAREN=41, 
		RPAREN=42, LBRACK=43, RBRACK=44, LBRAC=45, RBRAC=46, COMMA=47, COLON=48, 
		SEMICOLON=49, QUOT=50, LARROW=51, RARROW=52, WS=53, COMMENT=54, LINE_COMMENT=55;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"BOOLTYPE", "INTTYPE", "RATTYPE", "IF", "THEN", "ELSE", "IFF", "IMPLY", 
		"FORALL", "EXISTS", "OR", "AND", "NOT", "EQ", "NEQ", "LT", "LEQ", "GT", 
		"GEQ", "PLUS", "MINUS", "MUL", "DIV", "MOD", "REM", "PERCENT", "PRIME", 
		"TRUE", "FALSE", "ASSIGN", "HAVOC", "ASSUME", "INT", "NAT", "SIGN", "DOT", 
		"ID", "UNDERSCORE", "DIGIT", "LETTER", "LPAREN", "RPAREN", "LBRACK", "RBRACK", 
		"LBRAC", "RBRAC", "COMMA", "COLON", "SEMICOLON", "QUOT", "LARROW", "RARROW", 
		"WS", "COMMENT", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'bool'", "'int'", "'rat'", "'if'", "'then'", "'else'", "'iff'", 
		"'imply'", "'forall'", "'exists'", "'or'", "'and'", "'not'", "'='", "'/='", 
		"'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'mod'", "'rem'", 
		"'%'", null, "'true'", "'false'", "':='", "'havoc'", "'assume'", null, 
		null, null, "'.'", null, "'_'", null, null, "'('", "')'", "'['", "']'", 
		"'{'", "'}'", "','", "':'", "';'", null, "'<-'", "'->'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "BOOLTYPE", "INTTYPE", "RATTYPE", "IF", "THEN", "ELSE", "IFF", "IMPLY", 
		"FORALL", "EXISTS", "OR", "AND", "NOT", "EQ", "NEQ", "LT", "LEQ", "GT", 
		"GEQ", "PLUS", "MINUS", "MUL", "DIV", "MOD", "REM", "PERCENT", "PRIME", 
		"TRUE", "FALSE", "ASSIGN", "HAVOC", "ASSUME", "INT", "NAT", "SIGN", "DOT", 
		"ID", "UNDERSCORE", "DIGIT", "LETTER", "LPAREN", "RPAREN", "LBRACK", "RBRACK", 
		"LBRAC", "RBRAC", "COMMA", "COLON", "SEMICOLON", "QUOT", "LARROW", "RARROW", 
		"WS", "COMMENT", "LINE_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CoreDslLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoreDsl.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\29\u0148\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\23"+
		"\3\23\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31"+
		"\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\35"+
		"\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3"+
		"!\3!\3!\3!\3!\3!\3!\3\"\5\"\u00ee\n\"\3\"\3\"\3#\6#\u00f3\n#\r#\16#\u00f4"+
		"\3$\3$\5$\u00f9\n$\3%\3%\3&\3&\5&\u00ff\n&\3&\3&\3&\7&\u0104\n&\f&\16"+
		"&\u0107\13&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3"+
		"\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\64\3\65\3\65\3\65\3"+
		"\66\6\66\u012a\n\66\r\66\16\66\u012b\3\66\3\66\3\67\3\67\3\67\3\67\7\67"+
		"\u0134\n\67\f\67\16\67\u0137\13\67\3\67\3\67\3\67\3\67\3\67\38\38\38\3"+
		"8\78\u0142\n8\f8\168\u0145\138\38\38\3\u0135\29\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		")\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O"+
		")Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9\3\2\6\3\2\62;\4\2C\\"+
		"c|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u0151\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2"+
		"\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2"+
		"\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M"+
		"\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2"+
		"\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2"+
		"\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\3q\3\2\2\2\5v"+
		"\3\2\2\2\7z\3\2\2\2\t~\3\2\2\2\13\u0081\3\2\2\2\r\u0086\3\2\2\2\17\u008b"+
		"\3\2\2\2\21\u008f\3\2\2\2\23\u0095\3\2\2\2\25\u009c\3\2\2\2\27\u00a3\3"+
		"\2\2\2\31\u00a6\3\2\2\2\33\u00aa\3\2\2\2\35\u00ae\3\2\2\2\37\u00b0\3\2"+
		"\2\2!\u00b3\3\2\2\2#\u00b5\3\2\2\2%\u00b8\3\2\2\2\'\u00ba\3\2\2\2)\u00bd"+
		"\3\2\2\2+\u00bf\3\2\2\2-\u00c1\3\2\2\2/\u00c3\3\2\2\2\61\u00c5\3\2\2\2"+
		"\63\u00c9\3\2\2\2\65\u00cd\3\2\2\2\67\u00cf\3\2\2\29\u00d1\3\2\2\2;\u00d6"+
		"\3\2\2\2=\u00dc\3\2\2\2?\u00df\3\2\2\2A\u00e5\3\2\2\2C\u00ed\3\2\2\2E"+
		"\u00f2\3\2\2\2G\u00f8\3\2\2\2I\u00fa\3\2\2\2K\u00fe\3\2\2\2M\u0108\3\2"+
		"\2\2O\u010a\3\2\2\2Q\u010c\3\2\2\2S\u010e\3\2\2\2U\u0110\3\2\2\2W\u0112"+
		"\3\2\2\2Y\u0114\3\2\2\2[\u0116\3\2\2\2]\u0118\3\2\2\2_\u011a\3\2\2\2a"+
		"\u011c\3\2\2\2c\u011e\3\2\2\2e\u0120\3\2\2\2g\u0122\3\2\2\2i\u0125\3\2"+
		"\2\2k\u0129\3\2\2\2m\u012f\3\2\2\2o\u013d\3\2\2\2qr\7d\2\2rs\7q\2\2st"+
		"\7q\2\2tu\7n\2\2u\4\3\2\2\2vw\7k\2\2wx\7p\2\2xy\7v\2\2y\6\3\2\2\2z{\7"+
		"t\2\2{|\7c\2\2|}\7v\2\2}\b\3\2\2\2~\177\7k\2\2\177\u0080\7h\2\2\u0080"+
		"\n\3\2\2\2\u0081\u0082\7v\2\2\u0082\u0083\7j\2\2\u0083\u0084\7g\2\2\u0084"+
		"\u0085\7p\2\2\u0085\f\3\2\2\2\u0086\u0087\7g\2\2\u0087\u0088\7n\2\2\u0088"+
		"\u0089\7u\2\2\u0089\u008a\7g\2\2\u008a\16\3\2\2\2\u008b\u008c\7k\2\2\u008c"+
		"\u008d\7h\2\2\u008d\u008e\7h\2\2\u008e\20\3\2\2\2\u008f\u0090\7k\2\2\u0090"+
		"\u0091\7o\2\2\u0091\u0092\7r\2\2\u0092\u0093\7n\2\2\u0093\u0094\7{\2\2"+
		"\u0094\22\3\2\2\2\u0095\u0096\7h\2\2\u0096\u0097\7q\2\2\u0097\u0098\7"+
		"t\2\2\u0098\u0099\7c\2\2\u0099\u009a\7n\2\2\u009a\u009b\7n\2\2\u009b\24"+
		"\3\2\2\2\u009c\u009d\7g\2\2\u009d\u009e\7z\2\2\u009e\u009f\7k\2\2\u009f"+
		"\u00a0\7u\2\2\u00a0\u00a1\7v\2\2\u00a1\u00a2\7u\2\2\u00a2\26\3\2\2\2\u00a3"+
		"\u00a4\7q\2\2\u00a4\u00a5\7t\2\2\u00a5\30\3\2\2\2\u00a6\u00a7\7c\2\2\u00a7"+
		"\u00a8\7p\2\2\u00a8\u00a9\7f\2\2\u00a9\32\3\2\2\2\u00aa\u00ab\7p\2\2\u00ab"+
		"\u00ac\7q\2\2\u00ac\u00ad\7v\2\2\u00ad\34\3\2\2\2\u00ae\u00af\7?\2\2\u00af"+
		"\36\3\2\2\2\u00b0\u00b1\7\61\2\2\u00b1\u00b2\7?\2\2\u00b2 \3\2\2\2\u00b3"+
		"\u00b4\7>\2\2\u00b4\"\3\2\2\2\u00b5\u00b6\7>\2\2\u00b6\u00b7\7?\2\2\u00b7"+
		"$\3\2\2\2\u00b8\u00b9\7@\2\2\u00b9&\3\2\2\2\u00ba\u00bb\7@\2\2\u00bb\u00bc"+
		"\7?\2\2\u00bc(\3\2\2\2\u00bd\u00be\7-\2\2\u00be*\3\2\2\2\u00bf\u00c0\7"+
		"/\2\2\u00c0,\3\2\2\2\u00c1\u00c2\7,\2\2\u00c2.\3\2\2\2\u00c3\u00c4\7\61"+
		"\2\2\u00c4\60\3\2\2\2\u00c5\u00c6\7o\2\2\u00c6\u00c7\7q\2\2\u00c7\u00c8"+
		"\7f\2\2\u00c8\62\3\2\2\2\u00c9\u00ca\7t\2\2\u00ca\u00cb\7g\2\2\u00cb\u00cc"+
		"\7o\2\2\u00cc\64\3\2\2\2\u00cd\u00ce\7\'\2\2\u00ce\66\3\2\2\2\u00cf\u00d0"+
		"\7)\2\2\u00d08\3\2\2\2\u00d1\u00d2\7v\2\2\u00d2\u00d3\7t\2\2\u00d3\u00d4"+
		"\7w\2\2\u00d4\u00d5\7g\2\2\u00d5:\3\2\2\2\u00d6\u00d7\7h\2\2\u00d7\u00d8"+
		"\7c\2\2\u00d8\u00d9\7n\2\2\u00d9\u00da\7u\2\2\u00da\u00db\7g\2\2\u00db"+
		"<\3\2\2\2\u00dc\u00dd\7<\2\2\u00dd\u00de\7?\2\2\u00de>\3\2\2\2\u00df\u00e0"+
		"\7j\2\2\u00e0\u00e1\7c\2\2\u00e1\u00e2\7x\2\2\u00e2\u00e3\7q\2\2\u00e3"+
		"\u00e4\7e\2\2\u00e4@\3\2\2\2\u00e5\u00e6\7c\2\2\u00e6\u00e7\7u\2\2\u00e7"+
		"\u00e8\7u\2\2\u00e8\u00e9\7w\2\2\u00e9\u00ea\7o\2\2\u00ea\u00eb\7g\2\2"+
		"\u00ebB\3\2\2\2\u00ec\u00ee\5G$\2\u00ed\u00ec\3\2\2\2\u00ed\u00ee\3\2"+
		"\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\5E#\2\u00f0D\3\2\2\2\u00f1\u00f3"+
		"\5O(\2\u00f2\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4"+
		"\u00f5\3\2\2\2\u00f5F\3\2\2\2\u00f6\u00f9\5)\25\2\u00f7\u00f9\5+\26\2"+
		"\u00f8\u00f6\3\2\2\2\u00f8\u00f7\3\2\2\2\u00f9H\3\2\2\2\u00fa\u00fb\7"+
		"\60\2\2\u00fbJ\3\2\2\2\u00fc\u00ff\5Q)\2\u00fd\u00ff\5M\'\2\u00fe\u00fc"+
		"\3\2\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0105\3\2\2\2\u0100\u0104\5Q)\2\u0101"+
		"\u0104\5M\'\2\u0102\u0104\5O(\2\u0103\u0100\3\2\2\2\u0103\u0101\3\2\2"+
		"\2\u0103\u0102\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106"+
		"\3\2\2\2\u0106L\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u0109\7a\2\2\u0109N"+
		"\3\2\2\2\u010a\u010b\t\2\2\2\u010bP\3\2\2\2\u010c\u010d\t\3\2\2\u010d"+
		"R\3\2\2\2\u010e\u010f\7*\2\2\u010fT\3\2\2\2\u0110\u0111\7+\2\2\u0111V"+
		"\3\2\2\2\u0112\u0113\7]\2\2\u0113X\3\2\2\2\u0114\u0115\7_\2\2\u0115Z\3"+
		"\2\2\2\u0116\u0117\7}\2\2\u0117\\\3\2\2\2\u0118\u0119\7\177\2\2\u0119"+
		"^\3\2\2\2\u011a\u011b\7.\2\2\u011b`\3\2\2\2\u011c\u011d\7<\2\2\u011db"+
		"\3\2\2\2\u011e\u011f\7=\2\2\u011fd\3\2\2\2\u0120\u0121\7)\2\2\u0121f\3"+
		"\2\2\2\u0122\u0123\7>\2\2\u0123\u0124\7/\2\2\u0124h\3\2\2\2\u0125\u0126"+
		"\7/\2\2\u0126\u0127\7@\2\2\u0127j\3\2\2\2\u0128\u012a\t\4\2\2\u0129\u0128"+
		"\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c"+
		"\u012d\3\2\2\2\u012d\u012e\b\66\2\2\u012el\3\2\2\2\u012f\u0130\7\61\2"+
		"\2\u0130\u0131\7,\2\2\u0131\u0135\3\2\2\2\u0132\u0134\13\2\2\2\u0133\u0132"+
		"\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0136\3\2\2\2\u0135\u0133\3\2\2\2\u0136"+
		"\u0138\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u0139\7,\2\2\u0139\u013a\7\61"+
		"\2\2\u013a\u013b\3\2\2\2\u013b\u013c\b\67\2\2\u013cn\3\2\2\2\u013d\u013e"+
		"\7\61\2\2\u013e\u013f\7\61\2\2\u013f\u0143\3\2\2\2\u0140\u0142\n\5\2\2"+
		"\u0141\u0140\3\2\2\2\u0142\u0145\3\2\2\2\u0143\u0141\3\2\2\2\u0143\u0144"+
		"\3\2\2\2\u0144\u0146\3\2\2\2\u0145\u0143\3\2\2\2\u0146\u0147\b8\2\2\u0147"+
		"p\3\2\2\2\f\2\u00ed\u00f4\u00f8\u00fe\u0103\u0105\u012b\u0135\u0143\3"+
		"\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}