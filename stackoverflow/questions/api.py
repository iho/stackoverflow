from rest_framework import serializers, viewsets

from questions.models import Question, Answer, AnswerVote, QuestionVote
from questions.serializers import QuestionSerializer, QuestionVoteSerializer, AnswerSerializer, AnswerVoteSerializer
from questions.permissions import IsOwnerOrReadOnlyPermission

class QuestionViewSet(viewsets.ModelViewSet):
    queryset = Question.objects.all()
    serializer_class = QuestionSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]


class QuestionVoteViewSet(viewsets.ModelViewSet):
    queryset = QuestionVote.objects.all()
    serializer_class = QuestionVoteSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]

class AnswerViewSet(viewsets.ModelViewSet):
    queryset = Answer.objects.all()
    serializer_class = AnswerSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]


class AnswerVoteViewSet(viewsets.ModelViewSet):
    queryset = AnswerVote.objects.all()
    serializer_class = AnswerVoteSerializer
    permission_classes = [IsOwnerOrReadOnlyPermission]
