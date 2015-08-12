window.initEvents = (answerY, answerN) ->

  if not $("#"+answerN).prop 'checked'
    hideSelfEmployedNoWrap()

  $("#" + answerY).on "click", ->
    hideSelfEmployedNoWrap()

  $("#" + answerN).on "click", ->
    showSelfEmployedNoWrap()


showSelfEmployedNoWrap = ->
  $("#selfEmployedNoWrap").slideDown(0).attr 'aria-hidden', 'false'

hideSelfEmployedNoWrap = ->
  $("#selfEmployedNoWrap").slideUp(0).attr 'aria-hidden', 'true'
