package services

import play.api.test._
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.test.Helpers._
import anorm.~
import play.api.test.FakeApplication
import org.specs2.mutable.Around
import org.specs2.execute.{Result, AsResult}


object DBTests{

  def prepareDDL(implicit app: FakeApplication) = {
    DB.withConnection("carers"){implicit c =>
      SQL(
        """
        DROP TABLE  IF EXISTS transactionids CASCADE;
        CREATE TABLE transactionids (transaction_id CHARACTER(7) NOT NULL, createdon TIMESTAMP(6) DEFAULT now() NOT NULL, PRIMARY KEY (transaction_id));
        DROP TABLE IF EXISTS transactionstatus CASCADE;
        CREATE TABLE transactionstatus(
          transaction_id CHARACTER(7) NOT NULL,
          createdon TIMESTAMP(6) DEFAULT now() NOT NULL,
          status CHARACTER VARYING(10),
          type INTEGER,
          thirdparty INTEGER,
          circs_type INTEGER,
          lang CHARACTER VARYING(10),
          PRIMARY KEY(transaction_id),
          CONSTRAINT transaction_fk FOREIGN KEY(transaction_id) REFERENCES transactionids(transaction_id)
        );
        """
      ).execute()
    }
  }

  def newId(implicit app: FakeApplication) = {
    val id = (for(i <- 1 to 7)yield{ "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"((31 * Math.random()).toInt + 1)})(collection.breakOut)
    DB.withConnection("carers"){implicit c =>
      SQL("INSERT INTO transactionids (transaction_id) VALUES ({id});").on("id"->id).execute()
    }
    id
  }

  val parser = {
    get[String]("transaction_id") ~
    get[String]("status") ~
    get[Int]("type") ~
    get[Int]("thirdparty") ~
    get[Option[Int]]("circs_type") ~
    get[String]("lang") map {
      case id~status~typeI~thirdparty~circsType~lang => TransactionStatus(id,status,typeI,thirdparty,circsType, lang)
    }
  }

  def getId(id: String)(implicit app:FakeApplication):Option[TransactionStatus] = {
    import scala.language.postfixOps
    DB.withConnection("carers"){implicit c =>
      SQL(
        """
          SELECT transaction_id, status,type,thirdparty,circs_type,lang
          FROM transactionstatus
          WHERE transaction_id = {id}
        """
      ).on("id"->id)
       .as(parser singleOpt)
    }
  }


}

class WithApplicationAndDB extends WithApplication(app = FakeApplication(additionalConfiguration = inMemoryDatabase("carers",options=Map("MODE" -> "PostgreSQL","DB_CLOSE_DELAY"->"-1")))) with Around{

  override def around[T](t: => T)(implicit evidence$1: AsResult[T]): Result = {
    DBTests.prepareDDL
    super.around(t)
  }
}
