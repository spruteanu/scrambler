/*
 * Data Scrambler, Data Generation API
 * Copyright (c) 2015, Sergiu Prutean. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

import org.prismus.scrambler.ValuePredicates
import org.prismus.scrambler.value.IncrementalDate
import org.prismus.scrambler.value.IncrementalTypeValue
import org.prismus.scrambler.value.InstanceTypeValue
import org.prismus.scrambler.value.InstanceValueTypePredicate
import org.prismus.scrambler.value.RandomTypeValue
import org.prismus.scrambler.value.RandomUuid

/**
 * Default value definitions shipped with data scrambler project, used when no matches found in context
 *
 * @author Serge Pruteanu
 */

// if property name contains 'created' and type is java.util.Date generate date in a range of 5 years till current date
definition(ValuePredicates.predicateOf(~/(?i).*created.*/, Date), new IncrementalTypeValue(
        new IncrementalDate(new Date()).years(-5).next(), new Date()
))

// if property name contains 'modified' and type is java.util.Date generate date in a range of one month till current date
definition(ValuePredicates.predicateOf(~/(?i).*modified.*/, Date), new IncrementalTypeValue(
        new IncrementalDate(new Date()).months(-1).next(), new Date()
))

// if property ends with ID and is a Integer, and incremental Integer will be created starting from 1, step 1
definition(ValuePredicates.predicateOf(~/(?i).*id/, Integer), new IncrementalTypeValue(0))

// if property ends with ID and is a Long, and incremental Long will be created starting from 100_000L, step 100L
definition(ValuePredicates.predicateOf(~/(?i).*id/, Long), new IncrementalTypeValue(99_999L, 100L))

// if property ends with ID and is of type string, a random string will be generated
definition(ValuePredicates.predicateOf(~/(?i).*id/, String), new RandomUuid())

// anything that is not JDK object generate Instance value object
definition(new InstanceValueTypePredicate(), new InstanceTypeValue())

// anything else try to generate randomly (if supported)
definition(ValuePredicates.typePredicate(Object), new RandomTypeValue())
