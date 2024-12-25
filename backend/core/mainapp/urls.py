# mainapp/urls.py
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views_posts import PostViewSet
from .views_comments import CommentViewSet
from .views_auth import RegisterView, LoginView
from .views_hashtags import HashtagViewSet
from .views_users import UserProfileView

router = DefaultRouter()
router.register(r'posts', PostViewSet, basename='post')
router.register(r'comments', CommentViewSet, basename='comment')
router.register(r'hashtags', HashtagViewSet, basename='hashtags')

urlpatterns = [
    path('', include(router.urls)),
    path('auth/register/', RegisterView.as_view(), name='auth-register'),
    path('auth/login/', LoginView.as_view(), name='auth-login'),
    path('users/<int:pk>/', UserProfileView.as_view(), name='user-profile'),
]