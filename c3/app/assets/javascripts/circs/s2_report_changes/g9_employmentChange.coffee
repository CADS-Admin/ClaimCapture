window.stillCaring = (stillCaringY, stillCaringN, whenStoppedCaringDay, whenStoppedCaringMonth, whenStoppedCaringYear) ->
  if not $("#" + stillCaringN).prop("checked")
    hideStillCaringWrap(whenStoppedCaringDay, whenStoppedCaringMonth, whenStoppedCaringYear)

  $("#" + stillCaringN).on "click", ->
    showStillCaringWrap()

  $("#" + stillCaringY).on "click", ->
    hideStillCaringWrap(whenStoppedCaringDay, whenStoppedCaringMonth, whenStoppedCaringYear)

window.hasWorkStartedYet = (hasWorkStartedYetY, hasWorkStartedYetN, dateWhenStartedDay, dateWhenStartedMonth, dateWhenStartedYear, dateWhenWillItStartDay, dateWhenWillItStartMonth, dateWhenWillItStartYear, hasWorkFinishedYetYes, hasWorkFinishedYetNo, dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear) ->
  if not $("#" + hasWorkStartedYetY).prop("checked")
    hideHasWorkStartedYetWrap(dateWhenStartedDay, dateWhenStartedMonth, dateWhenStartedYear, hasWorkFinishedYetYes, hasWorkFinishedYetNo)
    hideHasWorkFinishedYetWrap(dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear)

  if not $("#" + hasWorkStartedYetN).prop("checked")
    hideWorkNotStartedYetWrap(dateWhenWillItStartDay, dateWhenWillItStartMonth, dateWhenWillItStartYear)

  $("#" + hasWorkStartedYetY).on "click", ->
    showHasWorkStartedYetWrap()
    hideWorkNotStartedYetWrap(dateWhenWillItStartDay, dateWhenWillItStartMonth, dateWhenWillItStartYear)

  $("#" + hasWorkStartedYetN).on "click", ->
    showWorkNotStartedYetWrap()
    hideHasWorkStartedYetWrap(dateWhenStartedDay, dateWhenStartedMonth, dateWhenStartedYear, hasWorkFinishedYetYes, hasWorkFinishedYetNo)
    hideHasWorkFinishedYetWrap(dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear)

window.hasWorkFinishedYet = (hasWorkFinishedYetY, hasWorkFinishedYetN, dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear) ->
  if not $("#" + hasWorkFinishedYetY).prop("checked")
    hideHasWorkFinishedYetWrap(dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear)

  $("#" + hasWorkFinishedYetY).on "click", ->
    showHasWorkFinishedYetWrap()

  $("#" + hasWorkFinishedYetN).on "click", ->
    hideHasWorkFinishedYetWrap(dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear)

window.typeOfWork = (typeOfWorkEmployed, typeOfWorkSelfEmployed, employerNameAndAddress1, employerNameAndAddress2, employerNameAndAddress3, employerPostcode, employerContactNumber, employerPayrollNumber, selfEmployedTypeOfWork, selfEmployedTotalIncomeYes, selfEmployedTotalIncomeNo, selfEmployedTotalIncomeDontKnow, selfEmployedMoreAboutChanges) ->
  if not $("#" + typeOfWorkEmployed).prop("checked")
    hideTypeOfWorkEmployedWrap(employerNameAndAddress1, employerNameAndAddress2, employerNameAndAddress3, employerPostcode, employerContactNumber, employerPayrollNumber)

  if not $("#" + typeOfWorkSelfEmployed).prop("checked")
    hideTypeOfWorkSelfEmployedWrap(selfEmployedTypeOfWork, selfEmployedTotalIncomeYes, selfEmployedTotalIncomeNo, selfEmployedTotalIncomeDontKnow, selfEmployedMoreAboutChanges)

  $("#" + typeOfWorkEmployed).on "click", ->
    showTypeOfWorkEmployedWrap()
    hideTypeOfWorkSelfEmployedWrap(selfEmployedTypeOfWork, selfEmployedTotalIncomeYes, selfEmployedTotalIncomeNo, selfEmployedTotalIncomeDontKnow, selfEmployedMoreAboutChanges)

  $("#" + typeOfWorkSelfEmployed).on "click", ->
    hideTypeOfWorkEmployedWrap(employerNameAndAddress1, employerNameAndAddress2, employerNameAndAddress3, employerPostcode, employerContactNumber, employerPayrollNumber)
    showTypeOfWorkSelfEmployedWrap()

showStillCaringWrap = () ->
  $("#stillCaringWrap").slideDown(0).attr 'aria-hidden', 'false'

hideStillCaringWrap = (whenStoppedCaringDay, whenStoppedCaringMonth, whenStoppedCaringYear) ->
  $("#stillCaringWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#stillCaringWrap input").val("")

showHasWorkStartedYetWrap = () ->
  $("#hasWorkStartedYetWrap").slideDown(0).attr 'aria-hidden', 'false'

hideWorkNotStartedYetWrap = (dateWhenWillItStartDay, dateWhenWillItStartMonth, dateWhenWillItStartYear) ->
  $("#workNotStartedYetWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#workNotStartedYetWrap input").val("")

showWorkNotStartedYetWrap = () ->
  $("#workNotStartedYetWrap").slideDown(0).attr 'aria-hidden', 'false'

hideHasWorkStartedYetWrap = (dateWhenStartedDay, dateWhenStartedMonth, dateWhenStartedYear, hasWorkFinishedYetYes, hasWorkFinishedYetNo) ->
  $("#hasWorkStartedYetWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#hasWorkStartedYetWrap input").val("")
  $("#hasWorkStartedYetWrap [type='radio']").prop("checked", false)

hideHasWorkFinishedYetWrap = (dateWhenFinishedDay, dateWhenFinishedMonth, dateWhenFinishedYear) ->
  $("#hasWorkFinishedYetWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#hasWorkFinishedYetWrap input").val("")

showHasWorkFinishedYetWrap = () ->
  $("#hasWorkFinishedYetWrap").slideDown(0).attr 'aria-hidden', 'false'

hideTypeOfWorkEmployedWrap = (employerNameAndAddress1, employerNameAndAddress2, employerNameAndAddress3, employerPostcode, employerContactNumber, employerPayrollNumber) ->
  $("#typeOfWorkEmployedWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#typeOfWorkEmployedWrap input").val("")

showTypeOfWorkEmployedWrap = () ->
  $("#typeOfWorkEmployedWrap").slideDown(0).attr 'aria-hidden', 'false'

hideTypeOfWorkSelfEmployedWrap = (selfEmployedTypeOfWork, selfEmployedTotalIncomeYes, selfEmployedTotalIncomeNo, selfEmployedTotalIncomeDontKnow, selfEmployedMoreAboutChanges) ->
  $("#typeOfWorkSelfEmployedWrap").slideUp(0).attr 'aria-hidden', 'true'
  $("#typeOfWorkSelfEmployedWrap input, #typeOfWorkSelfEmployedWrap textarea").val("")
  $("#typeOfWorkSelfEmployedWrap [type='radio']").prop("checked", false)

showTypeOfWorkSelfEmployedWrap = () ->
  $("#typeOfWorkSelfEmployedWrap").slideDown(0).attr 'aria-hidden', 'false'