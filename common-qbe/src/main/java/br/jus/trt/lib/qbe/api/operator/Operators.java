package br.jus.trt.lib.qbe.api.operator;

/**
 * Possui métodos de fábrica de todos os operadores nativos. 
 * @author augusto
 */
public class Operators {

	/** @see Equal*/
	public static Equal equal() { return new Equal(); }
	
	/** @see NotEqual*/
	public static NotEqual notEqual() {	return new NotEqual(); }
	
	/** @see Like*/
	public static Like like(final boolean caseSensitive) {	return new Like(caseSensitive);	}
	
	/** @see LikeSufix*/
	public static LikeSufix likeSufix(final boolean caseSensitive) {	return new LikeSufix(caseSensitive); }
	
	/** @see LikePrefix */
	public static LikePrefix likePrefix(final boolean caseSensitive) { return new LikePrefix(caseSensitive);	}	
	
	/** @see GreaterThan*/
	public static GreaterThan greater() { return new GreaterThan(false); }
	
	/** @see MaiorIgual*/
	public static GreaterThan greateEqual() { return new GreaterThan(true);	}
	
	/** @see LessThan*/
	public static LessThan less() { return new LessThan(false); }
	
	/** @see MenorIgual*/
	public static LessThan lessEqual() { return new LessThan(true); }
	
	/** @see In*/
	public static In in() { return new In(); }
	
	/** @see NotIn*/
	public static NotIn notIn() { return new NotIn(); }
	
	/** @see Between*/
	public static Between between() { return new Between(); }	
	
	/** @see IsNull*/
	public static IsNull isNull() { return new IsNull(); }		
	
	/** @see IsNotNull*/
	public static IsNotNull isNotNull() { return new IsNotNull(); }	
	
	/** @see IsEmpty*/
	public static IsEmpty isEmpty() { return new IsEmpty(); }		
	
	/** @see IsNotEmpty*/
	public static IsNotEmpty isNotEmpty() { return new IsNotEmpty(); }	
	
	/** @see SizeEqual*/
	public static SizeEqual sizeEqual() { return new SizeEqual(); }
	
	/** @see SizeNotEqual*/
	public static SizeNotEqual sizeNotEqual() { return new SizeNotEqual(); }
	
	/** @see SizeGreaterThan*/
	public static SizeGreaterThan sizeGreater() { return new SizeGreaterThan(false); }
	
	/** @see SizeGreaterThan*/
	public static SizeGreaterThan sizeGreaterEqual() { return new SizeGreaterThan(true); }
	
	/** @see SizeLessThan*/
	public static SizeLessThan sizeLess() { return new SizeLessThan(false); }
	
	/** @see SizeLessThan*/
	public static SizeLessThan sizeLessEquals() { return new SizeLessThan(true); }
	
}
