import { Outlet } from 'react-router-dom'
import NavigationRail from '../components/NavigationRail'

const AppLayout = () => {
  return (
    <div className="flex min-h-screen bg-[#e8edf5] text-slate-900">
      <NavigationRail />
      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  )
}

export default AppLayout

