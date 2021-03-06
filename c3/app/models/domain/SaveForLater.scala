package models.domain

case class SaveForLater(claim: Array[Byte],
                        location: String,
                        remainingAuthenticationAttempts: Int,
                        status: String,
                        applicationExpiry: Long,
                        cacheExpiryPeriod: Long,
                        appVersion: String
                       ) {

  def update(numberOfAuthenticationAttemptLeft: Int): SaveForLater = {
    copy(remainingAuthenticationAttempts = numberOfAuthenticationAttemptLeft)
  }

  def update(newStatus: String): SaveForLater = {
    copy(status = newStatus)
  }

  def update(newStatus: String, numberOfAuthenticationAttemptLeft: Int, newClaim: Array[Byte]): SaveForLater = {
    copy(status = newStatus, remainingAuthenticationAttempts = numberOfAuthenticationAttemptLeft, claim = newClaim)
  }

  def update(numberOfAuthenticationAttemptLeft: Int, newApplicationExpiry: Long, newCacheExpiryPeriod: Long): SaveForLater = {
    copy(remainingAuthenticationAttempts = numberOfAuthenticationAttemptLeft, applicationExpiry = newApplicationExpiry, cacheExpiryPeriod = newCacheExpiryPeriod)
  }
}

