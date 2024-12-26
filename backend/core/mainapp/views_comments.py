from django.shortcuts import get_object_or_404
from rest_framework import viewsets, status
from rest_framework.permissions import IsAuthenticatedOrReadOnly, AllowAny
from rest_framework.response import Response
from .models import Comment, Post
from .serializers import CommentSerializer


class CommentViewSet(viewsets.ModelViewSet):
    permission_classes = [AllowAny]
    queryset = Comment.objects.all()
    serializer_class = CommentSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def perform_create(self, serializer):
        post_id = self.request.data.get('post')
        post = get_object_or_404(Post, id=post_id)
        # Коли створюємо коментар, author береться з request.user
        serializer.save(author=self.request.user, post=post)

    def perform_update(self, serializer):
        serializer.save()
