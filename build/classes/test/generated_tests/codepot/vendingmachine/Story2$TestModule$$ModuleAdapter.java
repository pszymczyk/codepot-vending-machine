// Code generated by dagger-compiler.  Do not edit.
package codepot.vendingmachine;

import dagger.internal.ModuleAdapter;

/**
 * A manager of modules and provides adapters allowing for proper linking and
 * instance provision of types served by {@code @Provides} methods.
 */
public final class Story2$TestModule$$ModuleAdapter extends ModuleAdapter<Story2.TestModule> {
  private static final String[] INJECTS = { "members/codepot.vendingmachine.domain.VendingMachine", "members/codepot.vendingmachine.domain.Transaction", "codepot.vendingmachine.domain.TransactionFactory", };
  private static final Class<?>[] STATIC_INJECTIONS = { };
  private static final Class<?>[] INCLUDES = { };

  public Story2$TestModule$$ModuleAdapter() {
    super(codepot.vendingmachine.Story2.TestModule.class, INJECTS, STATIC_INJECTIONS, false /*overrides*/, INCLUDES, true /*complete*/, false /*library*/);
  }
}
