from django.urls import include, path

from rest_framework import routers

from questions.api import *


router = routers.DefaultRouter()
router.register(r'questions', QuestionViewSet)
router.register(r'question_votes', QuestionVoteViewSet)

router.register(r'anwers', AnswerViewSet)
router.register(r'answer_votes', AnswerVoteViewSet)

router.register(r'categories', CategoryViewSet)


urlpatterns = [
    path('', include(router.urls))
]