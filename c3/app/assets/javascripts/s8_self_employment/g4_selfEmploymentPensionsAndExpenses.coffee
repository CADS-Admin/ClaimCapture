window.initEvents = (answerY, answerN, doYouPayToLookAfterYourChildren, didYouPayToLookAfterThePersonYouCaredFor) ->
  $("#" + answerY).on "click", ->
    $("#selfEmployedPensionWrap").slideDown()
    $("#selfEmployedPensionWrap").css('display', "block")

  $("#" + answerN).on "click", ->
    $("#selfEmployedPensionWrap").slideUp()

  # we are returning a function here to assign it to 'conditionRequired' and which will be executed in trackSubmit.scala.html.
  return ->
    $("input[name=" + doYouPayToLookAfterYourChildren+"]:checked").val() == "no" &&
    $("input[name=" + didYouPayToLookAfterThePersonYouCaredFor+"]:checked").val() == "no"