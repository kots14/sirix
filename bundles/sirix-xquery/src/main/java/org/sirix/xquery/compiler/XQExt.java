package org.sirix.xquery.compiler;

import org.brackit.xquery.compiler.XQ;

/**
 * @author Sebastian Baechle
 * 
 */
public final class XQExt {

  private static final int OFFSET = XQ.allocate(1);

  public static final int MultiStepExpr = OFFSET + 0;

  public static final int IndexExpr = OFFSET + 1;

  public static final int ParentExpr = OFFSET + 2;

  public static final String NAMES[] = new String[] {"MultiStepExpr", "IndexExpr"};

  public static Object toName(int key) {
    return NAMES[key - OFFSET];
  }
}
