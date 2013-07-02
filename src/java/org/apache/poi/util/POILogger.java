/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A logger interface that strives to make it as easy as possible for
 * developers to write log calls, while simultaneously making those
 * calls as cheap as possible by performing lazy evaluation of the log
 * message.<p>
 *
 * @author Marc Johnson (mjohnson at apache dot org)
 * @author Glen Stampoultzis (glens at apache.org)
 * @author Nicola Ken Barozzi (nicolaken at apache.org)
 */
public abstract class POILogger {

    public static final int DEBUG = 1;
    public static final int INFO  = 3;
    public static final int WARN  = 5;
    public static final int ERROR = 7;
    public static final int FATAL = 9;

    /** Short strings for numeric log level. Use level as array index. */
    protected static final String LEVEL_STRINGS_SHORT[] = {"?", "D", "?", "I", "?", "W", "?", "E", "?", "F", "?"};
    /** Long strings for numeric log level. Use level as array index. */
    protected static final String LEVEL_STRINGS[] = {"?0?", "DEBUG", "?2?", "INFO", "?4?", "WARN", "?6?", "ERROR", "?8?", "FATAL", "?10+?"};


    /**
     * package scope so it cannot be instantiated outside of the util
     * package. You need a POILogger? Go to the POILogFactory for one
     */
    POILogger() {
        // no fields to initialise
    }

    abstract public void initialize(String cat);

    /**
     * Log a message
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 The object to log.  This is converted to a string.
     */
    abstract public void log(int level, Object obj1);

    /**
     * Log a message
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 The object to log.  This is converted to a string.
     * @param exception An exception to be logged
     */
    abstract public void log(int level, Object obj1,
                    final Throwable exception);


    /**
     * Check if a logger is enabled to log at the specified level
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     */
    abstract public boolean check(int level);

   /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first object to place in the message
     * @param obj2 second object to place in the message
     */
    public void log(int level, Object obj1, Object obj2)
    {
        if (check(level))
        {
            log(level, new StringBuilder(32).append(obj1).append(obj2));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3)
    {


        if (check(level))
        {
            log(level,
                    new StringBuilder(48).append(obj1).append(obj2)
                        .append(obj3));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     * @param obj4 fourth Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4)
    {


        if (check(level))
        {
            log(level,
                    new StringBuilder(64).append(obj1).append(obj2)
                        .append(obj3).append(obj4));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     * @param obj4 fourth Object to place in the message
     * @param obj5 fifth Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5)
    {


        if (check(level))
        {
            log(level,
                    new StringBuilder(80).append(obj1).append(obj2)
                        .append(obj3).append(obj4).append(obj5));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     * @param obj4 fourth Object to place in the message
     * @param obj5 fifth Object to place in the message
     * @param obj6 sixth Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6)
    {


        if (check(level))
        {
            log(level ,
                    new StringBuilder(96).append(obj1).append(obj2)
                        .append(obj3).append(obj4).append(obj5).append(obj6));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     * @param obj4 fourth Object to place in the message
     * @param obj5 fifth Object to place in the message
     * @param obj6 sixth Object to place in the message
     * @param obj7 seventh Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7)
    {


        if (check(level))
        {
            log(level,
                    new StringBuilder(112).append(obj1).append(obj2)
                        .append(obj3).append(obj4).append(obj5).append(obj6)
                        .append(obj7));
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third Object to place in the message
     * @param obj4 fourth Object to place in the message
     * @param obj5 fifth Object to place in the message
     * @param obj6 sixth Object to place in the message
     * @param obj7 seventh Object to place in the message
     * @param obj8 eighth Object to place in the message
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7, Object obj8)
    {


        if (check(level))
        {
            log(level,
                    new StringBuilder(128).append(obj1).append(obj2)
                        .append(obj3).append(obj4).append(obj5).append(obj6)
                        .append(obj7).append(obj8));
        }
    }

    /**
     * Log an exception, without a message
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param exception An exception to be logged
     */
    public void log(int level, final Throwable exception)
    {
        log(level, null, exception);
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(32).append(obj1).append(obj2),
                    exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param exception An error message to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(48).append(obj1).append(obj2)
                .append(obj3), exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param obj4 fourth object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4,
                    final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(64).append(obj1).append(obj2)
                .append(obj3).append(obj4), exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param obj4 fourth object to place in the message
     * @param obj5 fifth object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(80).append(obj1).append(obj2)
                .append(obj3).append(obj4).append(obj5), exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param obj4 fourth object to place in the message
     * @param obj5 fifth object to place in the message
     * @param obj6 sixth object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, final Throwable exception)
    {


        if (check(level))
        {
            log(level , new StringBuilder(96).append(obj1)
                .append(obj2).append(obj3).append(obj4).append(obj5)
                .append(obj6), exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param obj4 fourth object to place in the message
     * @param obj5 fifth object to place in the message
     * @param obj6 sixth object to place in the message
     * @param obj7 seventh object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7,
                    final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(112).append(obj1).append(obj2)
                .append(obj3).append(obj4).append(obj5).append(obj6)
                .append(obj7), exception);
        }
    }

    /**
     * Log a message. Lazily appends Object parameters together.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param obj1 first Object to place in the message
     * @param obj2 second Object to place in the message
     * @param obj3 third object to place in the message
     * @param obj4 fourth object to place in the message
     * @param obj5 fifth object to place in the message
     * @param obj6 sixth object to place in the message
     * @param obj7 seventh object to place in the message
     * @param obj8 eighth object to place in the message
     * @param exception An exception to be logged
     */
    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7, Object obj8,
                    final Throwable exception)
    {


        if (check(level))
        {
            log(level, new StringBuilder(128).append(obj1).append(obj2)
                .append(obj3).append(obj4).append(obj5).append(obj6)
                .append(obj7).append(obj8), exception);
        }
    }

    /**
     * Logs a formated message. The message itself may contain %
     * characters as place holders. This routine will attempt to match
     * the placeholder by looking at the type of parameter passed to
     * obj1.<p>
     *
     * If the parameter is an array, it traverses the array first and
     * matches parameters sequentially against the array items.
     * Otherwise the parameters after <code>message</code> are matched
     * in order.<p>
     *
     * If the place holder matches against a number it is printed as a
     * whole number. This can be overridden by specifying a precision
     * in the form %n.m where n is the padding for the whole part and
     * m is the number of decimal places to display. n can be excluded
     * if desired. n and m may not be more than 9.<p>
     *
     * If the last parameter (after flattening) is a Throwable it is
     * logged specially.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param message The message to log.
     * @param obj1 The first object to match against.
     */
    public void logFormatted(int level, String message,
                             Object obj1)
    {
        commonLogFormatted(level, message, new Object[]
        {
            obj1
        });
    }

    /**
     * Logs a formated message. The message itself may contain %
     * characters as place holders. This routine will attempt to match
     * the placeholder by looking at the type of parameter passed to
     * obj1.<p>
     *
     * If the parameter is an array, it traverses the array first and
     * matches parameters sequentially against the array items.
     * Otherwise the parameters after <code>message</code> are matched
     * in order.<p>
     *
     * If the place holder matches against a number it is printed as a
     * whole number. This can be overridden by specifying a precision
     * in the form %n.m where n is the padding for the whole part and
     * m is the number of decimal places to display. n can be excluded
     * if desired. n and m may not be more than 9.<p>
     *
     * If the last parameter (after flattening) is a Throwable it is
     * logged specially.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param message The message to log.
     * @param obj1 The first object to match against.
     * @param obj2 The second object to match against.
     */
    public void logFormatted(int level, String message,
                             Object obj1, Object obj2)
    {
        commonLogFormatted(level, message, new Object[]
        {
            obj1, obj2
        });
    }

    /**
     * Logs a formated message. The message itself may contain %
     * characters as place holders. This routine will attempt to match
     * the placeholder by looking at the type of parameter passed to
     * obj1.<p>
     *
     * If the parameter is an array, it traverses the array first and
     * matches parameters sequentially against the array items.
     * Otherwise the parameters after <code>message</code> are matched
     * in order.<p>
     *
     * If the place holder matches against a number it is printed as a
     * whole number. This can be overridden by specifying a precision
     * in the form %n.m where n is the padding for the whole part and
     * m is the number of decimal places to display. n can be excluded
     * if desired. n and m may not be more than 9.<p>
     *
     * If the last parameter (after flattening) is a Throwable it is
     * logged specially.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param message The message to log.
     * @param obj1 The first object to match against.
     * @param obj2 The second object to match against.
     * @param obj3 The third object to match against.
     */
    public void logFormatted(int level, String message,
                             Object obj1, Object obj2,
                             Object obj3)
    {
        commonLogFormatted(level, message, new Object[]
        {
            obj1, obj2, obj3
        });
    }

    /**
     * Logs a formated message. The message itself may contain %
     * characters as place holders. This routine will attempt to match
     * the placeholder by looking at the type of parameter passed to
     * obj1.<p>
     *
     * If the parameter is an array, it traverses the array first and
     * matches parameters sequentially against the array items.
     * Otherwise the parameters after <code>message</code> are matched
     * in order.<p>
     *
     * If the place holder matches against a number it is printed as a
     * whole number. This can be overridden by specifying a precision
     * in the form %n.m where n is the padding for the whole part and
     * m is the number of decimal places to display. n can be excluded
     * if desired. n and m may not be more than 9.<p>
     *
     * If the last parameter (after flattening) is a Throwable it is
     * logged specially.
     *
     * @param level One of DEBUG, INFO, WARN, ERROR, FATAL
     * @param message The message to log.
     * @param obj1 The first object to match against.
     * @param obj2 The second object to match against.
     * @param obj3 The third object to match against.
     * @param obj4 The forth object to match against.
     */
    public void logFormatted(int level, String message,
                             Object obj1, Object obj2,
                             Object obj3, Object obj4)
    {
        commonLogFormatted(level, message, new Object[]
        {
            obj1, obj2, obj3, obj4
        });
    }

    private void commonLogFormatted(int level, String message,
                                    Object [] unflatParams)
    {


        if (check(level))
        {
            Object[] params = flattenArrays(unflatParams);

            if (params[ params.length - 1 ] instanceof Throwable)
            {
                log(level, StringUtil.format(message, params),
                    ( Throwable ) params[ params.length - 1 ]);
            }
            else
            {
                log(level, StringUtil.format(message, params));
            }
        }
    }

    /**
     * Flattens any contained objects. Only tranverses one level deep.
     */
    private Object [] flattenArrays(Object [] objects)
    {
        List<Object> results = new ArrayList<>();

        for (final Object obj : objects)
        {
            results.addAll(objectToObjectArray(obj));
        }
        return results.toArray(new Object[ results.size() ]);
    }

    private List<Object> objectToObjectArray(Object object)
    {
        List<Object> results = new ArrayList<>();

        if (object instanceof byte [])
        {
            final byte[] arrays = ( byte [] ) object;

            for (final byte array : arrays)
            {
                results.add(Byte.valueOf(array));
            }
        }
        if (object instanceof char [])
        {
            final char[] arrays = ( char [] ) object;

            for (final char array : arrays)
            {
                results.add(Character.valueOf(array));
            }
        }
        else if (object instanceof short [])
        {
            final short[] arrays = ( short [] ) object;

            for (final short array : arrays)
            {
                results.add(Short.valueOf(array));
            }
        }
        else if (object instanceof int [])
        {
            final int[] arrays = ( int [] ) object;

            for (final int array : arrays)
            {
                results.add(Integer.valueOf(array));
            }
        }
        else if (object instanceof long [])
        {
            final long[] arrays = ( long [] ) object;

            for (final long array : arrays)
            {
                results.add(Long.valueOf(array));
            }
        }
        else if (object instanceof float [])
        {
            final float[] arrays = ( float [] ) object;

            for (final float array : arrays)
            {
                results.add(Float.valueOf(array));
            }
        }
        else if (object instanceof double [])
        {
            final double[] arrays = ( double [] ) object;

            for (final double array : arrays)
            {
                results.add(Double.valueOf(array));
            }
        }
        else if (object instanceof Object [])
        {
            final Object[] arrays = ( Object [] ) object;

            for (final Object array : arrays)
            {
                results.add(array);
            }
        }
        else
        {
            results.add(object);
        }
        return results;
    }
}
