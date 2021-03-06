/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.shadowmask.web.api

import org.datanucleus.store.schema.naming.ColumnType
import org.json4s._
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.FileUploadSupport
import org.scalatra.swagger._
import org.shadowmask.model.data.TitleType
import org.shadowmask.web.common.user.{ConfiguredAuthProvider, Token, User}
import org.shadowmask.web.model._
import org.shadowmask.web.service.HiveService

import scala.collection.JavaConverters._

class DataApi(implicit val swagger: Swagger) extends ScalatraServlet
  with FileUploadSupport
  with JacksonJsonSupport
  with SwaggerSupport
  with ConfiguredAuthProvider {

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected val applicationDescription: String = "DataApi"
  override protected val applicationName: Option[String] = Some("data")

  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }

  implicit def t2Some[T](t: T) = Some[T](t)

  val dataCloumnTypesGetOperation = (apiOperation[CloumnTypeResult]("dataCloumnTypesGet")
    summary "get all cloumn types"
    parameters (headerParam[String]("Authorization").description("authentication token"))
    )


  get("/columnTypes", operation(dataCloumnTypesGetOperation)) {
    val authToken = request.getHeader("Authorization")
    val u = getAuth().verify(Some(Token(authToken)))
    if (u == None) {
      halt(200, SimpleResult(Some(1), Some("authorization failed")), Map(), "");
    }
    CloumnTypeResult(
      Some(0),
      Some("ok"),
      Some(
        (for (t <- TitleType.values()) yield {
          CloumnType(t.name, t.desc, t.color)
        }).toList
      )
    )
  }


  val dataTaskGetOperation = (apiOperation[TaskResult]("dataTaskGet")
    summary "fetch all task in some state ."
    parameters(headerParam[String]("authorization").description(""),
    queryParam[String]("taskType").description("0-submited,1-finished,2-failed"),
    queryParam[String]("fetchType").description("0-all,1-paging"),
    queryParam[Int]("pageNum").description("index of page , 0-based ,default to 0").optional,
    queryParam[Int]("pageSize").description("pageSize").optional)
    )

  get("/task", operation(dataTaskGetOperation)) {

    val authToken = request.getHeader("Authorization")
    val u = getAuth().verify(Some(Token(authToken)))
    if (u == None) {
      halt(200, SimpleResult(Some(1), Some("authorization failed")), Map(), "");
    }
    val taskType = params.getAs[String]("taskType")
    val fetchType = params.getAs[String]("fetchType")
    val pageNum = params.getAs[Int]("pageNum")
    val pageSize = params.getAs[Int]("pageSize")

    fetchType.get match {
      case "0" => HiveService().getAllTask(taskType.get.toInt)
      case "1" => HiveService().getTaskListByPage(taskType.get.toInt, pageNum.get, pageSize.get)
      case _ => TaskResult(1,"unsupported fetch type",None)
    }
  }

  val dataSchemaGetOperation = (apiOperation[SchemaResult]("dataSchemaGet")
    summary "get schemas of datasources ."
    parameters(headerParam[String]("Authorization").description("authentication token"),
    queryParam[String]("source").description("database type, HIVE,SPARK, etc"))
    )


  get("/schema", operation(dataSchemaGetOperation)) {

    val authToken = request.getHeader("Authorization")
    val u = getAuth().verify(Some(Token(authToken)))
    if (u == None) {
      halt(200, SimpleResult(Some(1), Some("authorization failed")), Map(), "");
    }
    val source = params.getAs[String]("source")
    HiveService().getSchemaViewObject()
  }


  val dataTableGetOperation = (apiOperation[TableResult]("dataTableGet")
    summary "get n-first record of a table"
    parameters(headerParam[String]("Authorization").description("authentication token"),
    queryParam[String]("source").description("database type, HIVE,SPARK, etc"),
    queryParam[String]("datasetType").description("data set type ,TABLE,VIEW"),
    queryParam[String]("schema").description("the schema which the datasetType belongs to."),
    queryParam[String]("name").description("table/view name"),
    queryParam[Int]("rows").description("number of rows"))
    )

  get("/table", operation(dataTableGetOperation)) {


    val authToken = request.getHeader("Authorization")
    val u = getAuth().verify(Some(Token(authToken)))
    if (u == None) {
      halt(200, SimpleResult(Some(1), Some("authorization failed")), Map(), "");
    }
    val source = params.getAs[String]("source")
    val datasetType = params.getAs[String]("datasetType")
    val schema = params.getAs[String]("schema")
    val name = params.getAs[String]("name")
    val rows = params.getAs[Int]("rows")
    HiveService().getTableViewObject(source.get, schema.get, name.get, rows.get)
  }


}
