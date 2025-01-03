# mainapp/views_posts.py
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticatedOrReadOnly, AllowAny
from django.db.models import Q
from .models import Post
from .serializers import PostSerializer, PostListSerializer, PostDetailSerializer

class PostViewSet(viewsets.ModelViewSet):
    permission_classes = [AllowAny]
    queryset = Post.objects.all().order_by('-created_at')
    serializer_class = PostSerializer

    def get_serializer_class(self):
        if self.action == 'create':
            return PostSerializer
        elif self.action == 'retrieve':
            return PostDetailSerializer
        return PostListSerializer

    def get_queryset(self):
        queryset = super().get_queryset().prefetch_related('posthashtags__hashtag')
        hashtag = self.request.query_params.get('hashtag')
        if hashtag:
            hashtag = hashtag.lower().strip()
            queryset = queryset.filter(posthashtags__hashtag__name=hashtag)
        return queryset

    def perform_create(self, serializer):
        print('calling create model')
        serializer.save(author=self.request.user)
