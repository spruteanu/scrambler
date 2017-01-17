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
import org.prismus.scrambler.DataPredicates
import org.prismus.scrambler.data.IncrementalDate
import org.prismus.scrambler.data.IncrementalTypeData
import org.prismus.scrambler.data.InstanceTypeData
import org.prismus.scrambler.data.InstanceDataTypePredicate
import org.prismus.scrambler.data.RandomTypeData
import org.prismus.scrambler.data.RandomUuid

/**
 * Default object definitions shipped with object scrambler project, used when no matches found in context
 *
 * @author Serge Pruteanu
 */

// if property name contains 'created' and type is java.util.Date generate date in a range of 5 years till current date
definition(DataPredicates.matches(~/(?i).*created.*/, Date), new IncrementalTypeData(
        new IncrementalDate(new Date()).years(-5).next(), new Date()
))

// if property name contains 'modified' and type is java.util.Date generate date in a range of one month till current date
definition(DataPredicates.matches(~/(?i).*modified.*/, Date), new IncrementalTypeData(
        new IncrementalDate(new Date()).months(-1).next(), new Date()
))

// if property ends with ID and is a Integer, and incremental Integer will be created starting from 1, step 1
definition(DataPredicates.matches(~/(?i).*id/, Integer), new IncrementalTypeData(0))

// if property ends with ID and is a Long, and incremental Long will be created starting from 100_000L, step 100L
definition(DataPredicates.matches(~/(?i).*id/, Long), new IncrementalTypeData(99_999L, 100L))

// if property ends with ID and is of type string, a random string will be generated
definition(DataPredicates.matches(~/(?i).*id/, String), new RandomUuid())

// anything that is not JDK object generate Instance data object
definition(new InstanceDataTypePredicate(), new InstanceTypeData())

// anything else try to generate randomly (if supported)
definition(DataPredicates.isTypeOf(Object), new RandomTypeData())
