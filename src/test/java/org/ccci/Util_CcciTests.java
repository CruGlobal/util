package org.ccci;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  org.ccci.el._CcciElTests.class,
  org.ccci.faces._CcciFacesTests.class,
  org.ccci.util._CcciUtilTests.class
})
public class Util_CcciTests {
    // the class remains completely empty, 
    // being used only as a holder for the above annotations
    
    // can use @BeforeClass and @AfterClass methods!!!!
}