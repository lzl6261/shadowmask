package org.shadowmask.engine.spark.hierarchy.impl

import org.apache.spark.sql.UserDefinedFunction
import org.apache.spark.sql.functions.udf
import org.shadowmask.engine.spark.hierarchy.Hierarchy
import org.shadowmask.engine.spark.hierarchy.mask.IdRule

class IdHierarchy(alignLeft: Boolean,
                  maskLeft: Boolean,
                  maskChar: Char = '*') extends Hierarchy[String, String] {

  override def rootHierarchyLevel: Int = -1

  override def getUDF(hierarchy: Int): UserDefinedFunction = udf(getIdRule(hierarchy))

  def getIdRule(hierarchy: Int): (String) => String = {
    new IdRule(hierarchy, maskChar).mask
  }
}
