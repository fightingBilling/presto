/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.operator.window;

import org.testng.annotations.Test;

import static com.facebook.presto.SessionTestUtils.TEST_SESSION;
import static com.facebook.presto.spi.type.BigintType.BIGINT;
import static com.facebook.presto.spi.type.VarcharType.VARCHAR;
import static com.facebook.presto.testing.MaterializedResult.resultBuilder;

public class TestLastValueFunction
        extends AbstractTestWindowFunction
{
    @Test
    public void testLastValueUnbounded()
    {
        assertUnboundedWindowQuery("last_value(orderdate) OVER (PARTITION BY orderstatus ORDER BY orderkey)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, VARCHAR)
                        .row(3, "F", "1993-10-27")
                        .row(5, "F", "1993-10-27")
                        .row(6, "F", "1993-10-27")
                        .row(33, "F", "1993-10-27")
                        .row(1, "O", "1998-07-21")
                        .row(2, "O", "1998-07-21")
                        .row(4, "O", "1998-07-21")
                        .row(7, "O", "1998-07-21")
                        .row(32, "O", "1998-07-21")
                        .row(34, "O", "1998-07-21")
                        .build());
        assertUnboundedWindowQueryWithNulls("last_value(orderdate) OVER (PARTITION BY orderstatus ORDER BY orderkey)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, VARCHAR)
                        .row(3, "F", "1992-02-21")
                        .row(5, "F", "1992-02-21")
                        .row(null, "F", "1992-02-21")
                        .row(null, "F", "1992-02-21")
                        .row(34, "O", "1996-12-01")
                        .row(null, "O", "1996-12-01")
                        .row(1, null, "1995-07-16")
                        .row(7, null, "1995-07-16")
                        .row(null, null, "1995-07-16")
                        .row(null, null, "1995-07-16")
                        .build());

        assertUnboundedWindowQuery("last_value(orderkey) OVER (PARTITION BY orderstatus ORDER BY orderkey)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, BIGINT)
                        .row(3, "F", 33)
                        .row(5, "F", 33)
                        .row(6, "F", 33)
                        .row(33, "F", 33)
                        .row(1, "O", 34)
                        .row(2, "O", 34)
                        .row(4, "O", 34)
                        .row(7, "O", 34)
                        .row(32, "O", 34)
                        .row(34, "O", 34)
                        .build());
        assertUnboundedWindowQueryWithNulls("last_value(orderkey) OVER (PARTITION BY orderstatus ORDER BY orderkey)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, BIGINT)
                        .row(3, "F", null)
                        .row(5, "F", null)
                        .row(null, "F", null)
                        .row(null, "F", null)
                        .row(34, "O", null)
                        .row(null, "O", null)
                        .row(1, null, null)
                        .row(7, null, null)
                        .row(null, null, null)
                        .row(null, null, null)
                        .build());
    }

    @Test
    public void testLastValueBounded()
    {
        assertWindowQuery("last_value(orderkey) OVER (PARTITION BY orderstatus ORDER BY orderkey " +
                        "ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, BIGINT)
                        .row(3, "F", 6)
                        .row(5, "F", 33)
                        .row(6, "F", 33)
                        .row(33, "F", 33)
                        .row(1, "O", 4)
                        .row(2, "O", 7)
                        .row(4, "O", 32)
                        .row(7, "O", 34)
                        .row(32, "O", 34)
                        .row(34, "O", 34)
                        .build());
        assertWindowQueryWithNulls("last_value(orderkey) OVER (PARTITION BY orderstatus ORDER BY orderkey " +
                        "ROWS BETWEEN 2 PRECEDING AND 2 FOLLOWING)",
                resultBuilder(TEST_SESSION, BIGINT, VARCHAR, BIGINT)
                        .row(3, "F", null)
                        .row(5, "F", null)
                        .row(null, "F", null)
                        .row(null, "F", null)
                        .row(34, "O", null)
                        .row(null, "O", null)
                        .row(1, null, null)
                        .row(7, null, null)
                        .row(null, null, null)
                        .row(null, null, null)
                        .build());
    }
}
