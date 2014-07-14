/**************************************************************************
 *
 * Copyright (c) 2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

/**
 *
 * Provides classes used for compiling Tea programs.
 *
 * <p>In order to run a Tea program two steps must be performed.<p>
 *
 * <ul>
 *
 * <li> First the Tea program must be compiled by an <code>{@link
 * com.pdmfc.tea.compiler.SCompiler SCompiler}</code>. The result of
 * compiling a Tea program is an <code>{@link
 * com.pdmfc.tea.compiler.SCode SCode}</code> object.</li>
 *
 * <li>The second step involves the actual execution of the Tea
 * program. This is achieved by calling the <code>{@link
 * com.pdmfc.tea.compiler.SCode#exec(TeaContext)
 * SCode.exec(TeaContext)}</code> method on the <code>{@link
 * com.pdmfc.tea.compiler.SCode SCode}</code> obtained from the
 * compilation step.</li>
 *
 * </ul>
 *
 */
package com.pdmfc.tea.compiler;

