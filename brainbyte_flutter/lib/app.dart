import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/app_theme.dart';
import 'screens/onboarding_screen.dart';
import 'screens/main_shell.dart';
import 'state/app_state.dart';

class BrainByteApp extends ConsumerWidget {
  const BrainByteApp({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final state = ref.watch(appControllerProvider);
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'BrainByte',
      theme: BrainTheme.light(),
      darkTheme: BrainTheme.dark(),
      themeMode: state.darkMode ? ThemeMode.dark : ThemeMode.light,
      home: const LaunchGate(),
    );
  }
}

class LaunchGate extends ConsumerStatefulWidget {
  const LaunchGate({super.key});
  @override ConsumerState<LaunchGate> createState() => _LaunchGateState();
}
class _LaunchGateState extends ConsumerState<LaunchGate> with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  bool ready = false;
  @override void initState(){ super.initState(); _controller=AnimationController(vsync:this,duration:const Duration(milliseconds:1100))..forward(); Future.delayed(const Duration(milliseconds:1500),(){if(mounted)setState(()=>ready=true);}); }
  @override void dispose(){_controller.dispose();super.dispose();}
  @override Widget build(BuildContext context){
    final app=ref.watch(appControllerProvider);
    if(ready){ return AnimatedSwitcher(duration: app.reducedMotion?Duration.zero:const Duration(milliseconds:500),child: app.profile.onboarded?const MainShell():const OnboardingScreen()); }
    return Scaffold(body:Container(decoration:const BoxDecoration(gradient:LinearGradient(begin:Alignment.topLeft,end:Alignment.bottomRight,colors:[Color(0xFF3D2C8D),Color(0xFF7357FF),Color(0xFFB4A6FF)])),child:Center(child:AnimatedBuilder(animation:_controller,builder:(_,__){final v=Curves.easeOutBack.transform(_controller.value);return Column(mainAxisSize:MainAxisSize.min,children:[Transform.scale(scale:.7+.3*v,child:Container(width:104,height:104,decoration:BoxDecoration(color:Colors.white.withValues(alpha:.14),shape:BoxShape.circle,border:Border.all(color:Colors.white.withValues(alpha:.45))),child:const Icon(Icons.psychology_alt_rounded,color:Colors.white,size:60))),const SizedBox(height:24),Opacity(opacity:_controller.value,child:Transform.translate(offset:Offset(0,18*(1-_controller.value)),child:const Text('BrainByte',style:TextStyle(color:Colors.white,fontSize:38,fontWeight:FontWeight.w900,letterSpacing:-1)))),const SizedBox(height:8),Opacity(opacity:_controller.value,child:const Text('Feel better. Think clearer. Stay connected.',style:TextStyle(color:Colors.white70,fontSize:14,fontWeight:FontWeight.w600))) ]);}))));
  }
}
