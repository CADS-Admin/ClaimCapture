isChecked = (selector)  -> $("##{selector}").prop('checked')
val = (selector, text) -> if text? then $("##{selector}").val(text) else $("##{selector}").val()
S = (selector) -> $("##{selector}")

window.initEvents = (otherStarted_yes, otherStarted_no, otherStartedWrap, otherNotStartedWrap, otherStartDate,
  otherEndedDate, otherStartTime, otherEndedTime, someWhereElseDpWrap, someWhereElseYouWrap,
                     expectToCareAgain_yes, expectToCareAgain_no, expectToCareAgain_dontknow, expectYesWrap, expectNoWrap, dontknowWrap) ->

  if not isChecked(otherStarted_yes)
    hideWrapper(someWhereElseYouWrap)
    hideWrapper(someWhereElseDpWrap)
    hideWrapper(otherStartedWrap)

  if not isChecked(otherStarted_no)
    hideWrapper(otherNotStartedWrap)

  if not isChecked(expectToCareAgain_yes)
    hideWrapper(expectYesWrap)

  if not isChecked(expectToCareAgain_no)
    hideWrapper(expectNoWrap)

  if not isChecked(expectToCareAgain_dontknow)
    hideWrapper(dontknowWrap)

  S(otherStarted_yes).on "click", ->
    hideWrapper(otherNotStartedWrap)
    showWrapper(otherStartedWrap)

  S(otherStarted_no).on "click", ->
    hideWrapper(someWhereElseYouWrap)
    hideWrapper(someWhereElseDpWrap)
    hideWrapper(otherStartedWrap)
    showWrapper(otherNotStartedWrap)

  S(expectToCareAgain_yes).on "click", ->
    hideWrapper(expectNoWrap)
    hideWrapper(dontknowWrap)
    showWrapper(expectYesWrap)

  S(expectToCareAgain_no).on "click", ->
    hideWrapper(expectYesWrap)
    hideWrapper(dontknowWrap)
    showWrapper(expectNoWrap)

  S(expectToCareAgain_dontknow).on "click", ->
    hideWrapper(expectYesWrap)
    hideWrapper(expectNoWrap)
    showWrapper(dontknowWrap)

  if isNotMondayOrFriday(otherStartDate)
    hideTime(otherStartTime)

  if isNotMondayOrFriday(otherEndedDate)
    hideTime(otherEndedTime)

  dateOnChange(otherStartDate,(id)->
    isIt = isNotMondayOrFriday(id)
    hideTime(otherStartTime) if isIt
    showTime(otherStartTime) if not isIt
  )

  dateOnChange(otherEndedDate,(id)->
    isIt = isNotMondayOrFriday(id)
    hideTime(otherEndedTime) if isIt
    showTime(otherEndedTime) if not isIt
  )

showWrapper = (wrapper) ->
  $("#" + wrapper).slideDown(0).attr 'aria-hidden', 'false'

hideWrapper = (wrapper)->
  clearDownStreamInputs(wrapper)
  $("#" + wrapper).slideUp(0).attr 'aria-hidden', 'true'

clearDownStreamInputs = (wrapper)->
  $("#" + wrapper).find("input").each(clearInput)
  $("#" + wrapper).find("textarea").each(clearInput)

# If we want to also clear the validation error when item is hidden ?
# $("#" + wrapper).find(".validation-error").removeClass("validation-error")
# $("#" + wrapper).find(".validation-message").remove()
clearInput = ->
  if( $(this).attr("type") == "radio" )
    $(this).prop('checked', false)
    $(this).parent().removeClass("selected")
  else
    $(this).val("")

isNotMondayOrFriday = (id) ->
  date = getDate(id)
  not (date != undefined and (date.getDay() == 1 or date.getDay() == 5))

getDate = (id) ->
  values = $("#"+id+" li input").map( ->
    $(this).val()
  )
  m = parseInt(values[1],10)
  d = parseInt(values[0],10)
  y = parseInt(values[2],10)
  date = new Date(y,m-1,d)
  if date.getFullYear() == y and date.getMonth() + 1 == m and date.getDate() == d
    date
  else
    undefined

hideTime = (id) ->
  $("#"+id).val("")
  $("#"+id).parent("li").slideUp(0).attr 'aria-hidden', 'true'

showTime = (id) ->
  $("#"+id).parent("li").slideDown(0).attr 'aria-hidden', 'false'

dateOnChange = (id,f) ->
  $("#"+id+" li input").on "change paste keyup", ->
    f(id)

