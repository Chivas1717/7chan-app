# mainapp/views_posts.py
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from django.db.models import Q
from .models import Post
from .serializers import PostSerializer


class PostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all().order_by('-created_at')
    serializer_class = PostSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_serializer_class(self):
        if self.action == 'retrieve':
            return PostDetailSerializer
        return PostListSerializer

    def get_queryset(self):
        queryset = super().get_queryset()
        # Фільтрація за хештегом, якщо передано ?hashtag=music
        hashtag = self.request.query_params.get('hashtag')
        if hashtag:
            hashtag = hashtag.lower().strip()
            queryset = queryset.filter(hashtag__name=hashtag)
        return queryset

    def perform_create(self, serializer):
        # Автор – поточний юзер (з токена)
        serializer.save(author=self.request.user)
